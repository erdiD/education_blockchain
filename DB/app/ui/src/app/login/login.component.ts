import {Component, OnInit} from '@angular/core';
import {AuthService} from "../shared/services/auth.service";
import {Router} from "@angular/router";
import {MdDialog} from "@angular/material";
import {ModalDbIlvComponent} from "../standard-components/modal-db-ilv/modal-db-ilv.component";
import * as screenfull from 'screenfull';
import {StandardButtonDBILVComponent} from "../standard-components/standard-button-dbilv/standard-button-dbilv.component";
import {LoggingService} from "../shared/services/logging.service";


@Component({
  selector: 'app-login-component',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  public username: string;
  public password: string;
  public errorMessage;

  private loginBtn: StandardButtonDBILVComponent;

  constructor (private authService: AuthService,
               private router: Router,
               private logService: LoggingService,
               private dialogService: MdDialog) {
  }

  public ngOnInit () {
  }

  public login () {
    this.authService.login(this.username, this.password).subscribe((user) => {
      if (this.authService.isLoggedIn) {
        let redirect = this.authService.redirectUrl ? this.authService.redirectUrl : '/marketplace';
        this.router.navigate([redirect]);
      }
    }, err => {
      this.errorMessage = "Invalid user or password";
      this.loginBtn.isLoading = false;
      this.dialogService.open(ModalDbIlvComponent, {
        data: {
          modalType: ModalDbIlvComponent.WRONG_PASSWORD_OR_USERNAME
        }
      });
    });
  }

  public laddaIntercept (btn: StandardButtonDBILVComponent, callback: Function): void {
    this.loginBtn = btn;
    this.loginBtn.isLoading = true;
    callback.call(this);
  }

  public requestFullscreen (event: Event): void {
    if (screenfull.enabled) {
      console.log("TRY TO ENTER FULLSCREEN MODE");
      screenfull.request();
    }
  }
}
