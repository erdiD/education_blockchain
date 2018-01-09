import {Injectable} from '@angular/core';

import {Observable} from 'rxjs/Rx';
import 'rxjs/add/observable/of';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/delay';
import {User} from "../models/user.model";
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from "@angular/router";
import {ApiService} from "./api.service";
import {Headers, RequestOptionsArgs} from "@angular/http";
import {ReplaySubject} from "rxjs/ReplaySubject";
import { CookieService, CookieOptions } from 'angular2-cookie/core';
import {LoggingService} from "./logging.service";
import {isNullOrUndefined} from "util";

@Injectable()
export class AuthService implements CanActivate {
  private _isLoggedIn: boolean = false;
  private _redirectUrl: string;
  private _loggedUser: User;
  private replaySubject: ReplaySubject<User> = new ReplaySubject<User>(1);

  private _validUser: Map<string, string>;

  constructor (private apiService: ApiService,
               private router: Router,
               private logService: LoggingService,
               private cookieService: CookieService) {
  }

  public getLoggedUser (): Observable<User> {
    // this._userClientService.getLoggedUser().subscribe(user => {
    //   this._loggedUser = user;
    //   this._isLoggedIn = true;
    //   this.replaySubject.next(this._loggedUser);
    // });

    return this.replaySubject;
  }

  public canActivate (route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    let url: string = state.url;

    return this.checkLogin(url);
  }

  public checkLogin (url: string): boolean {

    if (this.isLoggedIn) {
      return true;
    } else if (this.getLoginCookie()) {
      this._loggedUser = User.fromJSON(this.getLoginCookie());
      this._isLoggedIn = true;
      this.logService.log("User already logged in");
/*
      this.logService.log(this._loggedUser);
*/

      return true;
    }

    // Store the attempted URL for redirecting
    this.redirectUrl = url;

    // Navigate to the login page with extras
    this.router.navigate(['/login']);
    return false;
  }

  public get loggedUser (): User {
    return this._loggedUser;
  }

  public saveLoginCookie (user: User): void {
    this.logService.log("Saving Login-Cookie");
    this.cookieService.removeAll();
    this.cookieService.putObject('ilvLogin', user);
  }

  public getLoginCookie (): Object {
    let cookie = this.cookieService.get('ilvLogin');
    if(!isNullOrUndefined(cookie)){
      return JSON.parse( cookie );
    }else{
      return null;
    }
  }

  public removeLoginCookie (): void {
    this.cookieService.removeAll();
  }

  public login (username: string, password: string): ReplaySubject<User> {

    this.replaySubject = new ReplaySubject<User>();

    //reset
    this._loggedUser = null;
    this._isLoggedIn = false;

    //login request
    let headers = new Headers();
    headers.append("Authorization", username);
    let data: any = {"username": username};

    let options: RequestOptionsArgs = {
      headers: headers
    };

    this.apiService.post("/session", data, options).map(response => User.fromJSON(response.json())).subscribe((user: User) => {

      this._loggedUser = user;
      this._isLoggedIn = true;

      if (this._loggedUser !== null) {
        this.saveLoginCookie(user);
        this.replaySubject.next(this._loggedUser);
      } else {
        this.replaySubject.error(new Error());
      }
    }, err => {
      this.replaySubject.error(err);
    });

    return this.replaySubject;

  }

  public logout (): void {
    this._isLoggedIn = false;
    this._loggedUser = null;
    this.removeLoginCookie();
  }

  get isLoggedIn (): boolean {
    return this._loggedUser ? true : false;
  }

  get redirectUrl (): string {
    return this._redirectUrl;
  }

  set redirectUrl (value: string) {
    this._redirectUrl = value;
  }
}
