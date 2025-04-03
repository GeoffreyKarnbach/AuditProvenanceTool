import { Component } from '@angular/core';
import { Router } from '@angular/router';
import {
  ConfirmationBoxService,
  ToastService,
  UserService,
} from 'src/app/services';

@Component({
  selector: 'app-user-page',
  templateUrl: './user-page.component.html',
  styleUrl: './user-page.component.scss',
})
export class UserPageComponent {
  constructor(
    private userService: UserService,
    private router: Router,
    private confirmationBoxService: ConfirmationBoxService,
    private toastService: ToastService
  ) {}

  deleteAccount(): void {
    this.confirmationBoxService
      .confirm(
        'Delete account',
        'Are you sure you want to delete your account?'
      )
      .then((confirmed) => {
        if (confirmed) {
          this.userService.deleteAccount().subscribe(
            () => {
              this.toastService.showSuccess('Account deleted', 'Success');
              this.router.navigate(['/']);
            },
            (error) => {
              this.toastService.showErrorResponse(error);
            }
          );
        }
      });
  }
}
