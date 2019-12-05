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
  submitted = false;
  returnUrl: string;
  loading = false;

  constructor(private formBuilder: FormBuilder, private authenticationService: AuthenticationService,
              private router: Router, private route: ActivatedRoute, private alertService: AlertService) {
    // redirect to home if already logged in.
    if (this.authenticationService.currentUserValue) {
      this.router.navigate(['/']);
    }
  }

  // getter for easy access to form fields
  get f() {
    return this.loginForm.controls;
  }

  ngOnInit() {
    // Initialize the form
    this.loginForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });

    this.returnUrl = this.route.snapshot.queryParams.returnUrl || '/';
  }

  // Login
  onSubmit() {
    this.submitted = true;
    if (this.loginForm.invalid) {
      return;
    }
    this.loading = true;
    this.authenticationService.login(this.f.username.value, this.f.password.value).pipe(first())
      .subscribe(
        data => {
          this.router.navigate([this.returnUrl]);
        }, (error: HttpErrorResponse) => {
          this.alertService.error(error);
          this.loading = false;
        });
  }


}
