import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {DemandService} from "./shared/demand.service";
import { DemandComponent } from './demand.component';
import {StandardComponentsModule} from "../standard-components/standard-components.module";
import {DemandFormComponent} from "./demand-form/demand-form.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {
  MdCheckboxModule, MdDatepickerModule, MdDialogModule, MdInputModule, MdNativeDateModule,
  MdRadioModule, MdSelectModule
} from "@angular/material";
import {DemandRouting} from "./demand.routing";
import {FileUploadModule} from "ng2-file-upload";

@NgModule({
  imports: [
    CommonModule,
    StandardComponentsModule,
    FormsModule,
    ReactiveFormsModule,
    DemandRouting,
    MdInputModule,
    MdCheckboxModule,
    MdDatepickerModule,
    MdNativeDateModule,
    MdSelectModule,
    MdRadioModule,
    MdDialogModule,
    FileUploadModule
  ],
  declarations: [
    DemandComponent,
    DemandFormComponent
  ],
  providers:[
    DemandService
  ]
})
export class DemandModule { }
