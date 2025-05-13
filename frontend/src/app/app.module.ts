import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ExamplePageComponent } from './components/pages/example-page/example-page.component';
import { ExampleSharedComponent } from './components/shared/example-shared/example-shared.component';
import { RouterModule } from '@angular/router';
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
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { WorkflowPageComponent } from './components/pages/workflow-page/workflow-page.component';
import { UnificationPageComponent } from './components/pages/unification-page/unification-page.component';

@NgModule({
  declarations: [
    AppComponent,
    ExamplePageComponent,
    ExampleSharedComponent,
    ToastComponent,
    HeaderComponent,
    ImageViewComponent,
    CustomCalendarComponent,
    FooterComponent,
    WorkflowPageComponent,
    UnificationPageComponent,
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
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
