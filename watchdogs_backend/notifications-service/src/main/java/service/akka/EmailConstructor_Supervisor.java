//package service.akka;
//
//import akka.actor.*;
//
//import akka.routing.FromConfig;
//import akka.routing.RoundRobinPool;
//import service.akka.AkkaMessages.ClusterRelatedMessages.EmailNotificationRequest;
//
//public class EmailConstructor_Supervisor extends AbstractActor {
//
//    private final ActorRef emailDeliveryRouter;
//
//    private ActorRef deliveryPartner;
//
//    static Props props(final int numberOf_Construction_Workers, final int numberOf_Delivery_Workers,
//                       ActorRef deliveryPartner) {
//        return Props.create(EmailConstructor_Supervisor.class,
//                () -> new EmailConstructor_Supervisor(numberOf_Construction_Workers, numberOf_Delivery_Workers ,
//                        deliveryPartner));
//    }
//
//    public EmailConstructor_Supervisor(int numberOf_Construction_Workers, int numberOf_Delivery_Workers,
//                                       ActorRef notificationService_Supervisor) {
//        for(int i = 0; i < numberOf_Construction_Workers; i++){
//            ActorRef theCreatedConstructor = getContext()
//                    .actorOf(FromConfig.getInstance().props(Props.create(EmailConstructor_Supervisor.class)));
//            this.getContext().watch(theCreatedConstructor);
//        }
//
//        this.emailDeliveryRouter = this.getContext().actorOf(
//                new RoundRobinPool(numberOf_Delivery_Workers)
//                        .props(Props.create(EmailConstructor_Worker_Actor.class)),
//                "emailDeliveryWorkers");
//
//        this.deliveryPartner = deliveryPartner;
//    }
//
//    @Override
//    public void preStart() {
//        // TODO: SETUP
//    }
//
//    @Override
//    public Receive createReceive() {
//        return receiveBuilder()
//                .matchEquals(
//                        EmailNotificationRequest.class,
//                        emailNotificationMessage -> {
//                            emailConstructorRouter.tell(emailNotificationMessage, getSelf());
//                        })
//
//                .build();
//    }
//}