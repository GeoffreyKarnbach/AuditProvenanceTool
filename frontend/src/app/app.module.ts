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
import { AiWorkflowVisualizerComponent } from './components/shared/ai-workflow-visualizer/ai-workflow-visualizer.component';
import { PqClarificationCardComponent } from './components/shared/pq-clarification-card/pq-clarification-card.component';
import { PqClarificationSectionComponent } from './components/shared/pq-clarification-section/pq-clarification-section.component';
import { RecapCardComponent } from './components/shared/recap-card/recap-card.component';
import { ResultPageComponent } from './components/pages/result-page/result-page.component';

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
    AiWorkflowVisualizerComponent,
    PqClarificationCardComponent,
    PqClarificationSectionComponent,
    RecapCardComponent,
    ResultPageComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    RouterModule,
    NgbModule,
    FormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    FormsModule,
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
