import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UsersService} from '../../services/users.service';
import {Router} from '@angular/router';
import {AlertService} from '../../services/alert.service';
import {User} from '../../models/user';
import {HttpErrorResponse} from '@angular/common/http';

/**
 * Component to handle user registration
 */
@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup; // Form object
  submitted = false; // Signifies form is submitted
  loading = false; // Signifies form is loading

  constructor(private formBuilder: FormBuilder, private userService: UsersService, private router: Router,
              private alertService: AlertService) {
  }

  /**
   * getter for easy access to form fields
   */
  get f() {
    return this.registerForm.controls;
  }

  /**
   * Initialization hook
   */
  ngOnInit() {
    // Initialize the form
    this.registerForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(20)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      passwordConfirm: ['', Validators.required]
    });
  }

  /**
   * Action on user submitting the form
   */
  onSubmit() {
    this.submitted = true;
    // Confirm passwords
    if (this.registerForm.get('password').value !== this.registerForm.get('passwordConfirm').value) {
      this.registerForm.get('passwordConfirm').setErrors({notmatch: 'Passwords don\'t match'})
      return;
    }
    // Invalid form
    if (this.registerForm.invalid) {
      return;
    }

    this.loading = true;
    // Get user details from the form.
    const user = new User(-1, this.registerForm.get('username').value, this.registerForm.get('email').value,
      this.registerForm.get('passwordConfirm').value);
    // Submit details to server.
    this.userService.register(user).subscribe(
      // Success
      data => {
        this.alertService.success('Registration successful! Please Log in to continue', true);
        this.router.navigate(['/']);
      },
      // Error
      (error: HttpErrorResponse) => {
        this.alertService.error(error);
      }
    );
    this.loading = false;
  }
}
