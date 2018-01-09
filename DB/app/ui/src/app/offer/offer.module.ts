import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {OfferService} from "./shared/offer.service";
import {StandardComponentsModule} from "../standard-components/standard-components.module";
import {OfferComponent} from "./offer.component";
import {OfferFormComponent} from "./offer-form/offer-form.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {
  MdCheckboxModule, MdDatepickerModule, MdDialogModule, MdInputModule, MdNativeDateModule, MdRadioModule,
  MdSelectModule
} from "@angular/material";
import {OfferRouting} from "./offer.routing";
import {FileUploadModule} from "ng2-file-upload";
import {FileUploadAreaService} from "../standard-components/file-upload-area/file-upload-area.service";

@NgModule({
  imports: [
    CommonModule,
    StandardComponentsModule,
    FormsModule,
    OfferRouting,
    ReactiveFormsModule,
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
    OfferComponent,
    OfferFormComponent
  ],
  providers:[
    OfferService,
    FileUploadAreaService
  ]
})
export class OfferModule { }
