import { NgModule } from '@angular/core';
import {APP_BASE_HREF, CommonModule} from '@angular/common';
import {RouterModule} from "@angular/router";
import {BrowserModule} from "@angular/platform-browser";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpModule} from "@angular/http";

import {SplashscreenModule} from "./splashscreen/splashscreen.module";
import {LoginModule} from "./login/login.module";
import {MarketModule} from "./market/market.module";
import {ProjectModule} from "./project/project.module";
import {DemandModule} from "./demand/demand.module";
import {OfferModule} from "./offer/offer.module";
import {StandardComponentsModule} from "./standard-components/standard-components.module";
import {ApiService} from "./shared/services/api.service";
import {LoggingService} from "./shared/services/logging.service";
import {AuthService} from "./shared/services/auth.service";
import {AppComponent} from "./app.component";
import {AppRoutingModule} from "./app-routing.module";
import {StatusNotificationService} from "./standard-components/services/status-notification.service";
import {BackgroundService} from "./standard-components/services/background.service";
import {CookieOptions, CookieService} from "angular2-cookie/core";
import {ProjectHeaderService} from "./standard-components/services/project-header.service";




@NgModule({
  imports: [
    CommonModule,
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpModule,
    BrowserAnimationsModule,
    RouterModule,
    AppRoutingModule,
    SplashscreenModule,
    LoginModule,
    MarketModule,
    ProjectModule,
    DemandModule,
    OfferModule,
    StandardComponentsModule
  ],
  providers:[
    ApiService,
    CookieService,
    LoggingService,
    AuthService,
    StatusNotificationService,
    BackgroundService,
    ProjectHeaderService,
    {provide: 'Window', useValue: window},
    { provide: CookieOptions, useValue: {} }
  ],
  declarations: [AppComponent],
  bootstrap: [AppComponent]
})
export class AppModule { }
