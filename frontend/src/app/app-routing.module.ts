import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ExamplePageComponent } from './components';
import { WorkflowPageComponent } from './components/pages/workflow-page/workflow-page.component';
import { UnificationPageComponent } from './components/pages/unification-page/unification-page.component';
import { ResultPageComponent } from './components/pages/result-page/result-page.component';

const routes: Routes = [
  { path: 'workflow', component: WorkflowPageComponent },
  { path: 'unification/:processId', component: UnificationPageComponent },
  { path: 'result/:processId', component: ResultPageComponent },
  { path: '', component: ExamplePageComponent },
  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
