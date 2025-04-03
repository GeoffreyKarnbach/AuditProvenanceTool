import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LoginRegisterDto } from 'src/app/dtos';
import { ToastService, UserService } from 'src/app/services';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss'],
})
export class LoginPageComponent {
  constructor(
    private userService: UserService,
    private toastService: ToastService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (localStorage.getItem('token')) {
      this.router.navigate(['/']);
    }
  }

  loginDto: LoginRegisterDto = {
    username: '',
    password: '',
  };

  passwordVisible: boolean = false;

  login(): void {
    this.userService.login(this.loginDto).subscribe(
      (response) => {
        this.toastService.showSuccess('Successfully logged in', 'Success');
        this.router.navigate(['/']);
      },
      (error) => {
        this.toastService.showErrorResponse(error);
      }
    );
  }

  togglePasswordVisibility(): void {
    this.passwordVisible = !this.passwordVisible;
  }
}
