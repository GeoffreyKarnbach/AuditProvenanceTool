import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {
  ExamplePageComponent,
  LoginPageComponent,
  RegisterPageComponent,
  UserPageComponent,
} from './components';
import { AuthGuard } from './guards';

const routes: Routes = [
  { path: 'register', component: RegisterPageComponent },
  { path: 'login', component: LoginPageComponent },
  { path: 'user', component: UserPageComponent, canActivate: [AuthGuard] },
  { path: '**', component: ExamplePageComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
