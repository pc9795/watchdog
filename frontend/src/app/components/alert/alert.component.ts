import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs/internal/Subscription';
import {AlertService} from '../../services/alert.service';

/**
 * A generic component to show error messages.
 */
@Component({
  selector: 'app-alert',
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.css']
})
export class AlertComponent implements OnInit, OnDestroy {
  private subscription: Subscription;
  message: any;

  constructor(private alertService: AlertService) {
  }

  /**
   * Initialization hook
   */
  ngOnInit() {
    this.subscription = this.alertService.getMessage().subscribe(
      message => {
        this.message = message;
      }
    );
  }

  /**
   * Delete hook
   */
  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }


}
