package service.akka;

import service.akka.AkkaMessages.ClusterRelatedMessages.EmailNotificationRequest;
import service.akka.AkkaMessages.ClusterRelatedMessages.JobFailed;

//import static service.akka.AkkaMessages.ClusterRelatedMessages.JobFailed;
//import static service.akka.AkkaMessages.ClusterRelatedMessages.TransformationJob;
//import static service.akka.AkkaMessages.ClusterRelatedMessages.TransformationResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.concurrent.ThreadLocalRandom;

import akka.actor.ActorSelection;
import akka.actor.Address;
import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.cluster.ClusterEvent.ReachableMember;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class NotificationService_Cluster_Actor extends AbstractActor {

    final String notificationServicePath;
    final Set<Address> notificationServices_Nodes = new HashSet<Address>();

    LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    Cluster cluster = Cluster.get(getContext().getSystem());

    public NotificationService_Cluster_Actor(String notificationServicePath) {
        this.notificationServicePath = notificationServicePath;
    }

    // subscribe to cluster changes, MemberUp
    @Override
    public void preStart() {
        cluster.subscribe(getSelf(), MemberUp.class, ClusterEvent.ReachabilityEvent.class);


    }

    // re-subscribe when restart
    @Override
    public void postStop() {
        cluster.unsubscribe(getSelf());
    }

    // subscribe to cluster changes
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals(
                        EmailNotificationRequest.class,
                        x -> !notificationServices_Nodes.isEmpty(),
                        x -> {
                            // just pick any one
                            List<Address> nodesList = new ArrayList<Address>(notificationServices_Nodes);
                            Address address =
                                    nodesList.get(ThreadLocalRandom.current().nextInt(nodesList.size()));
                            ActorSelection service = getContext().actorSelection(address + notificationServicePath);
                            service.forward(notificationServicePath, getContext());
                        })
                .match(JobFailed.class, System.out::println)
                .match(
                        CurrentClusterState.class,
                        state -> {
                            notificationServices_Nodes.clear();
                            for (Member member : state.getMembers()) {
                                if (member.hasRole("compute") && member.status().equals(MemberStatus.up())) {
                                    notificationServices_Nodes.add(member.address());
                                }
                            }
                        })
                .match(
                        MemberUp.class,
                        memberAdding -> {
                            if (memberAdding.member().hasRole("compute")) notificationServices_Nodes.add(memberAdding.member().address());
                        })
                .match(
                        MemberEvent.class,
                        event -> {
                            notificationServices_Nodes.remove(event.member().address());
                        })
                .match(
                        UnreachableMember.class,
                        unreachable -> {
                            notificationServices_Nodes.remove(unreachable.member().address());
                        })
                .match(
                        ReachableMember.class,
                        reachable -> {
                            if (reachable.member().hasRole("compute")) notificationServices_Nodes.add(reachable.member().address());
                        })
                .build();
    }




}
