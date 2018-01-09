import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {LoginComponent} from "./login.component";
import {MdDialogModule, MdInputModule} from "@angular/material";
import {FormsModule} from "@angular/forms";
import {StandardComponentsModule} from "../standard-components/standard-components.module";
import {LoginRouting} from "./login.routing";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    StandardComponentsModule,
    LoginRouting,
    MdInputModule,
    MdDialogModule
  ],
  declarations: [
    LoginComponent
  ]
})
export class LoginModule { }
