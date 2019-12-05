import {Routes, RouterModule} from '@angular/router';
import {RegisterComponent} from './components/register/register.component';
import {LoginComponent} from './components/login/login.component';
import {HomeComponent} from './components/home/home.component';
import {AuthGuard} from './auth.guard';
import {AddMonitorComponent} from './components/add-monitor/add-monitor.component';
import {ViewMonitorComponent} from './components/view-monitor/view-monitor.component';

/**
 * All the routesof the application.
 */
const appRoutes: Routes = [
  {path: '', component: HomeComponent, canActivate: [AuthGuard]},
  {path: 'createMonitor', component: AddMonitorComponent, canActivate: [AuthGuard]},
  {path: 'viewMonitor', component: ViewMonitorComponent, canActivate: [AuthGuard]},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},

  // otherwise redirect to home
  {path: '**', redirectTo: ''}
];

export const routing = RouterModule.forRoot(appRoutes);
