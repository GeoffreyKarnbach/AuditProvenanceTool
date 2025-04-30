import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ExamplePageComponent } from './components';
import { WorkflowPageComponent } from './components/pages/workflow-page/workflow-page.component';

const routes: Routes = [
  { path: 'workflow', component: WorkflowPageComponent },
  { path: '', component: ExamplePageComponent },
  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
