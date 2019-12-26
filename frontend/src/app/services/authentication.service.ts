import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs/internal/BehaviorSubject';
import {User} from '../models/user';
import {Observable} from 'rxjs/internal/Observable';
import {HttpClient} from '@angular/common/http';
import {map} from 'rxjs/operators';
import {AlertService} from './alert.service';
import {environment} from '../../environments/environment';

/**
 * For authentication related methods
 */
@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private currentUserSubject: BehaviorSubject<User>;
  public currentUser: Observable<User>;

  constructor(private http: HttpClient, private alertService: AlertService) {
    this.currentUserSubject = new BehaviorSubject<User>(JSON.parse(localStorage.getItem('currentUser')));
    this.currentUser = this.currentUserSubject.asObservable();
  }

  /**
   * Get the logged in user's username
   */
  public get currentUserValue(): User {
    return this.currentUserSubject.value;
  }

  /**
   * Log out
   */
  logout() {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
    this.http.get(`${environment.server}logout`, {withCredentials: true}).subscribe(
      // Success
      data => {
      },
      // Error
      error => {
        this.alertService.error(error);
      }
    );
  }

  /**
   * Login
   */
  login(username: string, password: string) {
    return this.http.post(`${environment.server}login`, {
      username,
      password
    }, {withCredentials: true}).pipe(map(user => {
      if (user) {
        // Cache the user in local storage.
        localStorage.setItem('currentUser', JSON.stringify(user));
        this.currentUserSubject.next(user as User);
      }
      return user;
    }));
  }
}
