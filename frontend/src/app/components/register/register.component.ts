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
  submitted = false;
  loading = false;

  constructor(private formBuilder: FormBuilder, private userService: UsersService, private router: Router,
              private alertService: AlertService) {
  }

  // getter for easy access to form fields
  get f() {
    return this.registerForm.controls;
  }

  ngOnInit() {
    // Initialize the form
    this.registerForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(20)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      passwordConfirm: ['', Validators.required]
    });
  }

  // Submit a user registration
  onSubmit() {
    this.submitted = true;
    // confirm passwords
    if (this.registerForm.get('password').value !== this.registerForm.get('passwordConfirm').value) {
      this.registerForm.get('passwordConfirm').setErrors({notmatch: 'Passwords don\'t match'})
      return;
    }

    if (this.registerForm.invalid) {
      return;
    }

    // Submit details to server.
    this.loading = true;
    const user = new User(-1, this.registerForm.get('username').value, this.registerForm.get('email').value,
      this.registerForm.get('passwordConfirm').value);
    this.userService.register(user).subscribe(
      data => {
        this.alertService.success('Registration successful! Please Log in to continue', true);
        this.router.navigate(['/']);
      }, (error: HttpErrorResponse) => {
        this.alertService.error(error);
      }
    );
    this.loading = false;
  }
}
