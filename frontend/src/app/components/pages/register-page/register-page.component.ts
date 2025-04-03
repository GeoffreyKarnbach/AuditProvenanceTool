import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoginRegisterDto } from 'src/app/dtos';
import { ToastService, UserService } from 'src/app/services';

@Component({
  selector: 'app-register-page',
  templateUrl: './register-page.component.html',
  styleUrls: ['./register-page.component.scss'],
})
export class RegisterPageComponent implements OnInit {
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

  registerDto: LoginRegisterDto = {
    username: '',
    password: '',
  };

  confirmPassword: string = '';
  passwordVisible: boolean = false;
  confirmPasswordVisible: boolean = false;

  register(): void {
    if (this.registerDto.password !== this.confirmPassword) {
      console.log(this.registerDto);
    }

    if (this.registerDto.username.trim() === '') {
      console.log('Username is empty');
    }

    if (this.registerDto.password.trim() === '') {
      console.log('Password is empty');
    }

    this.userService.register(this.registerDto).subscribe(
      (response) => {
        this.toastService.showSuccess('Successfully registered', 'Success');
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

  toggleConfirmPasswordVisibility(): void {
    this.confirmPasswordVisible = !this.confirmPasswordVisible;
  }
}
