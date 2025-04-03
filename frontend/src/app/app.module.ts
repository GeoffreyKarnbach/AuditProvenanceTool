import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ExamplePageComponent } from './components/pages/example-page/example-page.component';
import { ExampleSharedComponent } from './components/shared/example-shared/example-shared.component';
import { RouterModule } from '@angular/router';
import { LoginPageComponent } from './components/pages/login-page/login-page.component';
import { RegisterPageComponent } from './components/pages/register-page/register-page.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import {
  CustomCalendarComponent,
  FooterComponent,
  HeaderComponent,
  ImageViewComponent,
  ToastComponent,
} from './components';
import { UserPageComponent } from './components/pages/user-page/user-page.component';
import { httpInterceptorProviders } from './interceptors';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@NgModule({
  declarations: [
    AppComponent,
    ExamplePageComponent,
    ExampleSharedComponent,
    LoginPageComponent,
    RegisterPageComponent,
    ToastComponent,
    HeaderComponent,
    UserPageComponent,
    ImageViewComponent,
    CustomCalendarComponent,
    FooterComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    RouterModule,
    NgbModule,
    FormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent],
})
export class AppModule {}
