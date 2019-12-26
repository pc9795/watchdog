import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from '../../services/authentication.service';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {first} from 'rxjs/operators';
import {AlertService} from '../../services/alert.service';
import {HttpErrorResponse} from '@angular/common/http';

/**
 * Component to handle login
 */
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup; // Form object
  submitted = false; // Signifies form is submitted
  returnUrl: string; // url which was accessed while unauthorised; From this url the user was redirected to login page.
  loading = false; // Signifies form is loading

  constructor(private formBuilder: FormBuilder, private authenticationService: AuthenticationService,
              private router: Router, private route: ActivatedRoute, private alertService: AlertService) {
    // Redirect to home if already logged in.
    if (this.authenticationService.currentUserValue) {
      this.router.navigate(['/']);
    }
  }

  /**
   * getter for easy access to form fields
   */
  get f() {
    return this.loginForm.controls;
  }

  /**
   * Initialization hook
   */
  ngOnInit() {
    // Initialize the form
    this.loginForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });

    this.returnUrl = this.route.snapshot.queryParams.returnUrl || '/';
  }

  /**
   * Action on submitting form
   */
  onSubmit() {
    this.submitted = true;
    // Invalid form
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.authenticationService.login(this.f.username.value, this.f.password.value).pipe(first())
      .subscribe(
        // Success
        data => {
          this.router.navigate([this.returnUrl]);
        }, (
          // Error
          error: HttpErrorResponse) => {
          this.alertService.error(error);
          this.loading = false;
        });
    this.loading = false;

  }
}
