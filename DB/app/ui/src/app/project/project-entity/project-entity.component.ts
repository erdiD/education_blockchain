import {Component, EventEmitter, Inject, Input, OnInit, Output} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {MdDialog} from "@angular/material";
import {isNullOrUndefined} from "util";

import {LoggingService} from "../../shared/services/logging.service";
import {IProjectEntity} from "../interfaces/IProjectEntity";
import {ProjectService} from "../shared/project.service";
import {StatusNotificationService} from "../../standard-components/services/status-notification.service";
import {IDemand} from "../interfaces/IDemand";
import {ModalDbIlvComponent} from "../../standard-components/modal-db-ilv/modal-db-ilv.component";
import {Actions} from "../../shared/types/action.types";
import {EntityTypes} from "../../shared/types/entitiy.types";
import {StandardButtonDBILVComponent} from "../../standard-components/standard-button-dbilv/standard-button-dbilv.component";
import {States} from "../../shared/types/states.types";

import {PdfService} from "../../standard-components/services/pdf.service";

@Component({
  selector: 'app-project-entity',
  templateUrl: './project-entity.component.html',
  styleUrls: ['./project-entity.component.scss'],
  providers: [LoggingService]
})
export class ProjectEntityComponent implements OnInit {

  @Input() projectEntity: IProjectEntity;
  @Output() followRoute = new EventEmitter();

  public EntityTypes = EntityTypes;
  public Actions = Actions;
  public lastClickedActionButton: StandardButtonDBILVComponent;

  constructor (private logService: LoggingService,
               private route: ActivatedRoute,
               private router: Router,
               private projectService: ProjectService,
               private statusNotificationService: StatusNotificationService,
               private dialogService: MdDialog,
               private pdfService: PdfService,
               @Inject('Window') private window: any) {
  }

  public ngOnInit () {
    this.logService.log("Create new Project-Entity of Progress-Type: ");
    this.logService.log(this.projectEntity);
  }

  public hasOffers (): boolean {
    // i replaced this with hasOpenOffers() - not sure if this is currently used anymore.
    // TODO: Test if this is removeable
    if (this.projectEntity.type === EntityTypes.ENTITY_TYPE_DEMAND) {
      let demandStep: IDemand = <IDemand>this.projectEntity;
      if (!isNullOrUndefined(demandStep.offers) && demandStep.offers.length > 0) {
        return true;
      }
    }
    return false;
  }

  public hasOpenOffers (): boolean {
    if (this.projectEntity.type === EntityTypes.ENTITY_TYPE_DEMAND) {
      let demandStep: IDemand = <IDemand>this.projectEntity;
      if (!isNullOrUndefined(demandStep.offers) && demandStep.offers.length > 0) {
        for (let offer of demandStep.offers) {
          if(offer.state !== States.OFFER_CLOSED){
            return true;
          }
        }
      }
    }
    return false;
  }

  public laddaIntercept (btn: StandardButtonDBILVComponent, callback: Function): void {
    this.lastClickedActionButton = btn;
    callback.call(this);
  }

  public onAskQuestionClick (event: Event): void {
    this.logService.log("ASK QUESTIONS CLICKED");
    this.window.open(this.projectEntity.messageBoardUrl, "_blank");
  }

  public onSubmitOfferClick (event: Event): void {
    this.logService.log("SUBMIT OFFER CLICKED");
    let dialogRef = this.dialogService.open(ModalDbIlvComponent, {
      data: {
        modalType: ModalDbIlvComponent.MODAL_TYPE_SUBMIT_OFFER
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.action === ModalDbIlvComponent.ACCEPT) {
        this.lastClickedActionButton.isLoading = true;
        this.projectService.updateOfferState(this.projectEntity.id, Actions.SUBMIT_OFFER).subscribe((resp) => {
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_OFFER_SUBMITTED);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.lastClickedActionButton.isLoading = false;
            this.router.navigate(['/marketplace']);
          });
        });
      }
    });
  }

  public onAcceptOfferClick (event: Event): void {
    this.logService.log("ACCEPT OFFER CLICKED");
    let dialogRef = this.dialogService.open(ModalDbIlvComponent, {
      data: {
        modalType: ModalDbIlvComponent.MODAL_TYPE_ACCEPT_OFFER
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.action === ModalDbIlvComponent.ACCEPT) {
        this.lastClickedActionButton.isLoading = true;
        this.projectService.updateOfferState(this.projectEntity.id, Actions.ACCEPT_OFFER).subscribe((resp) => {
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_OFFER_ACCEPTED);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.lastClickedActionButton.isLoading = false;
            this.router.navigate(['/marketplace']);
          });
        });
      }
    });
  }

  public onRejectOfferClick (event: Event): void {
    this.logService.log("REJECT OFFER CLICKED");
    let dialogRef = this.dialogService.open(ModalDbIlvComponent, {
      data: {
        modalType: ModalDbIlvComponent.MODAL_TYPE_REJECT_OFFER
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.action === ModalDbIlvComponent.ACCEPT) {
        this.lastClickedActionButton.isLoading = true;
        this.projectService.updateOfferState(this.projectEntity.id, Actions.REJECT_OFFER).subscribe((resp) => {
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_OFFER_REJECTED);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.lastClickedActionButton.isLoading = false;
            this.router.navigate(['/marketplace']);
          });
        });
      }
    });
  }

  public onApproveOfferClick (event: Event): void {
    this.logService.log("APPROVE OFFER CLICKED");
    let dialogRef = this.dialogService.open(ModalDbIlvComponent, {
      data: {
        modalType: ModalDbIlvComponent.MODAL_TYPE_APPROVE_OFFER
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.action === ModalDbIlvComponent.ACCEPT) {
        this.lastClickedActionButton.isLoading = true;
        this.projectService.updateOfferState(this.projectEntity.id, Actions.APPROVE_OFFER).subscribe((resp) => {
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_OFFER_APPROVED);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.lastClickedActionButton.isLoading = false;
            this.router.navigate(['/marketplace']);
          });
        }, err => {
          // no errors
        });
      }
    });
  }

  public onApproveOfferInternalClick (event: Event): void {
    this.logService.log("APPROVE_OFFER_INTERNAL OFFER CLICKED");
    let dialogRef = this.dialogService.open(ModalDbIlvComponent, {
      data: {
        modalType: ModalDbIlvComponent.MODAL_TYPE_APPROVE_OFFER_INTERNAL
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.action === ModalDbIlvComponent.ACCEPT) {
        this.lastClickedActionButton.isLoading = true;
        this.projectService.updateOfferState(this.projectEntity.id, Actions.APPROVE_OFFER_INTERNAL).subscribe((resp) => {
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_OFFER_APPROVED);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.lastClickedActionButton.isLoading = false;
            this.router.navigate(['/marketplace']);
          });
        });
      }
    });
  }
  public onCloseOfferClick (event: Event): void {
    this.logService.log("CLOSE OFFER CLICKED");
    let dialogRef = this.dialogService.open(ModalDbIlvComponent, {
      data: {
        modalType: ModalDbIlvComponent.MODAL_TYPE_CLOSE_OFFER
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.action === ModalDbIlvComponent.ACCEPT) {
        this.lastClickedActionButton.isLoading = true;
        this.projectService.updateOfferState(this.projectEntity.id, Actions.CLOSE_OFFER).subscribe((resp) => {
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_OFFER_CLOSED);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.lastClickedActionButton.isLoading = false;
            this.router.navigate(['/marketplace']);
          });
        });
      }
    });
  }

  public onResignOfferClick (event: Event): void {
    this.logService.log("RESIGN OFFER CLICKED");
    let dialogRef = this.dialogService.open(ModalDbIlvComponent, {
      data: {
        modalType: ModalDbIlvComponent.MODAL_TYPE_RESIGN_OFFER
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.action === ModalDbIlvComponent.ACCEPT) {
        this.lastClickedActionButton.isLoading = true;
        this.projectService.updateOfferState(this.projectEntity.id, Actions.RESIGN_OFFER).subscribe((resp) => {
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_OFFER_RESIGNED);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.lastClickedActionButton.isLoading = false;
            this.router.navigate(['/marketplace']);
          });
        });
      }
    });
  }

  public onCompleteOfferClick (event: Event): void {
    this.logService.log("COMPLETE OFFER CLICKED");
    let dialogRef = this.dialogService.open(ModalDbIlvComponent, {
      data: {
        modalType: ModalDbIlvComponent.MODAL_TYPE_COMPLETE_OFFER
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.action === ModalDbIlvComponent.ACCEPT) {
        this.lastClickedActionButton.isLoading = true;
        this.projectService.updateOfferState(this.projectEntity.id, Actions.CLOSE_OFFER).subscribe((resp) => {
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_OFFER_COMPLETED);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.lastClickedActionButton.isLoading = false;
            this.router.navigate(['/marketplace']);
          });
        });
      }
    });
  }

  public onRevokeOfferClick (event: Event): void {
    this.logService.log("REVOKE OFFER CLICKED");
    let dialogRef = this.dialogService.open(ModalDbIlvComponent, {
      data: {
        modalType: ModalDbIlvComponent.MODAL_TYPE_REVOKE_OFFER
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.action === ModalDbIlvComponent.ACCEPT) {
        this.lastClickedActionButton.isLoading = true;
        // this.projectService.updateOfferState(this.projectEntity.id, Actions.CLOSE_OFFER).subscribe((resp) => {
        this.projectService.updateOfferState(this.projectEntity.id, Actions.REVOKE_OFFER).subscribe((resp) => {
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_OFFER_REVOKED);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.lastClickedActionButton.isLoading = false;
            this.router.navigate(['/marketplace']);
          });
        });
      }
    });
  }

  public onActivateOfferClick (event: Event): void {
    this.logService.log("ACTIVATE OFFER CLICKED");
    let dialogRef = this.dialogService.open(ModalDbIlvComponent, {
      data: {
        modalType: ModalDbIlvComponent.MODAL_TYPE_ACTIVATE_OFFER
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.action === ModalDbIlvComponent.ACCEPT) {
        this.lastClickedActionButton.isLoading = true;
        this.projectService.updateOfferState(this.projectEntity.id, Actions.CLOSE_OFFER).subscribe((resp) => {
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_OFFER_ACTIVATED);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.lastClickedActionButton.isLoading = false;
            this.router.navigate(['/marketplace']);
          });
        });
      }
    });
  }

  public onReworkOfferClick (event: Event): void {
    this.logService.log("REWORK OFFER CLICKED - WHAT DOES THAT MEAN???");
  }

  public onReviewOfferClick (event: Event): void {
    this.logService.log("ANY ACTION HERE???");
    this.logService.log("ASK FOR REVIEW_OFFER CLICKED");
    let dialogRef = this.dialogService.open(ModalDbIlvComponent, {
      data: {
        modalType: ModalDbIlvComponent.MODAL_TYPE_REVIEW_OFFER
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.action === ModalDbIlvComponent.ACCEPT) {
        this.lastClickedActionButton.isLoading = true;
        this.projectService.updateOfferState(this.projectEntity.id, Actions.REVIEW_OFFER).subscribe((resp) => {
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_OFFER_REVIEW_REQEST);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.lastClickedActionButton.isLoading = false;
            this.router.navigate(['/marketplace']);
          });
        });
      }
    });
  }

  public onRejectDemandClick (event: Event): void {
    this.logService.log("REJECT DEMAND CLICKED");
    let dialogRef = this.dialogService.open(ModalDbIlvComponent, {
      data: {
        modalType: ModalDbIlvComponent.MODAL_TYPE_REJECT_DEMAND
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.action === ModalDbIlvComponent.ACCEPT) {
        this.lastClickedActionButton.isLoading = true;
        this.projectService.updateDemandState(this.projectEntity.id, Actions.REJECT_DEMAND).subscribe((resp) => {
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_DEMAND_REJECTED);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.lastClickedActionButton.isLoading = false;
            this.router.navigate(['/marketplace']);
          });
        });
      }
    });
  }

  public onSubmitDemandClick (event: Event): void {
    this.logService.log("SUBMIT DEMAND CLICKED");
    let dialogRef = this.dialogService.open(ModalDbIlvComponent, {
      data: {
        modalType: ModalDbIlvComponent.MODAL_TYPE_SUBMIT_DEMAND
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.action === ModalDbIlvComponent.ACCEPT) {
        this.lastClickedActionButton.isLoading = true;

        this.projectService.updateDemandState(this.projectEntity.id, Actions.SUBMIT_DEMAND).subscribe((resp) => {
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_DEMAND_SUBMITTED, "Vorgangsnummer: " + this.projectEntity.projectID);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.lastClickedActionButton.isLoading = false;
            this.router.navigate(['/marketplace']);
          });
        });
      }
    });
  }

  public onApproveDemandClick (event: Event): void {
    this.logService.log("APPROVE DEMAND CLICKED");
    let dialogRef = this.dialogService.open(ModalDbIlvComponent, {
      data: {
        modalType: ModalDbIlvComponent.MODAL_TYPE_APPROVE_DEMAND
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.action === ModalDbIlvComponent.ACCEPT) {
        this.lastClickedActionButton.isLoading = true;
        this.projectService.updateDemandState(this.projectEntity.id, Actions.APPROVE_DEMAND).subscribe((resp) => {
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_DEMAND_APPROVED);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.lastClickedActionButton.isLoading = false;
            this.router.navigate(['/marketplace']);
          });
        });
      }
    });
  }

  public onAcceptDemandClick (event: Event): void {
    this.logService.log("ACCEPT DEMAND CLICKED");
    let dialogRef = this.dialogService.open(ModalDbIlvComponent, {
      data: {
        modalType: ModalDbIlvComponent.MODAL_TYPE_ACCEPT_DEMAND
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.action === ModalDbIlvComponent.ACCEPT) {
        this.lastClickedActionButton.isLoading = true;
        this.projectService.updateDemandState(this.projectEntity.id, Actions.ACCEPT_DEMAND).subscribe((resp) => {
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_DEMAND_ACCEPTED);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.lastClickedActionButton.isLoading = false;
            this.router.navigate(['/marketplace']);
          });
        });
      }
    });
  }

  public onRevokeDemandClick (event: Event): void {
    this.logService.log("REVOKE DEMAND CLICKED");
    let dialogRef = this.dialogService.open(ModalDbIlvComponent, {
      data: {
        modalType: ModalDbIlvComponent.MODAL_TYPE_REVOKE_DEMAND
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.action === ModalDbIlvComponent.ACCEPT) {
        this.lastClickedActionButton.isLoading = true;
        this.projectService.updateDemandState(this.projectEntity.id, Actions.REVOKE_DEMAND).subscribe((resp) => {
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_DEMAND_REVOKED);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.lastClickedActionButton.isLoading = false;
            this.router.navigate(['/marketplace']);
          });
        });
      }
    });
  }

  public onResignDemandClick (event: Event): void {
    this.logService.log("RESIGN DEMAND CLICKED");
    let dialogRef = this.dialogService.open(ModalDbIlvComponent, {
      data: {
        modalType: ModalDbIlvComponent.MODAL_TYPE_RESIGN_DEMAND
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.action === ModalDbIlvComponent.ACCEPT) {
        this.lastClickedActionButton.isLoading = true;
        this.projectService.updateDemandState(this.projectEntity.id, Actions.RESIGN_DEMAND).subscribe((resp) => {
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_DEMAND_RESIGNED);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.lastClickedActionButton.isLoading = false;
            this.router.navigate(['/marketplace']);
          });
        });
      }
    });
  }

  public onBlockDemand (event: Event): void {
    this.logService.log("BLOCK DEMAND CLICKED");
    let dialogRef = this.dialogService.open(ModalDbIlvComponent, {
      data: {
        modalType: ModalDbIlvComponent.MODAL_TYPE_BLOCK_DEMAND
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.action === ModalDbIlvComponent.ACCEPT) {
        this.lastClickedActionButton.isLoading = true;
        this.projectService.updateDemandState(this.projectEntity.id, Actions.BLOCK_DEMAND).subscribe((resp) => {
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_DEMAND_BLOCKED);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.lastClickedActionButton.isLoading = false;
            this.router.navigate(['/marketplace']);
          });
        });
      }
    });
  }

  public onCloseDemand (event: Event): void {
    this.logService.log("CLOSE DEMAND CLICKED");
    let dialogRef = this.dialogService.open(ModalDbIlvComponent, {
      data: {
        modalType: ModalDbIlvComponent.MODAL_TYPE_CLOSE_DEMAND
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.action === ModalDbIlvComponent.ACCEPT) {
        this.lastClickedActionButton.isLoading = true;
        this.projectService.updateDemandState(this.projectEntity.id, Actions.CLOSE_DEMAND).subscribe((resp) => {
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_DEMAND_CLOSED);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.router.navigate(['marketplace']);
          });
        });
      }
    });
  }

  public onCompleteDemandClick (event: Event): void {
    this.logService.log("COMPLETE DEMAND CLICKED");
    let dialogRef = this.dialogService.open(ModalDbIlvComponent, {
      data: {
        modalType: ModalDbIlvComponent.MODAL_TYPE_COMPLETE_DEMAND
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.action === ModalDbIlvComponent.ACCEPT) {
        this.lastClickedActionButton.isLoading = true;
        this.projectService.updateDemandState(this.projectEntity.id, Actions.COMPLETE_DEMAND).subscribe((resp) => {
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_DEMAND_COMPLETED);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.lastClickedActionButton.isLoading = false;
            this.router.navigate(['/marketplace']);
          });
        });
      }
    });
  }

  public onMakeOfferClick (event: Event): void {
    this.logService.log("POST OFFER CLICKED");
    this.logService.log(this.projectEntity);
    this.router.navigate(['offer/' + this.projectEntity.id + '/newOffer']);
  }

  public onAskForApprovalClick (event: Event): void {
    this.logService.log("COMPLETE DEMAND CLICKED");
    let dialogRef = this.dialogService.open(ModalDbIlvComponent, {
      data: {
        modalType: ModalDbIlvComponent.MODAL_TYPE_ASK_FOR_APPROVAL
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.action === ModalDbIlvComponent.ACCEPT) {
        this.lastClickedActionButton.isLoading = true;
        this.projectService.updateDemandState(this.projectEntity.id, Actions.ASK_FOR_APPROVAL).subscribe((resp) => {
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_ASK_FOR_APPROVAL);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.lastClickedActionButton.isLoading = false;
            this.router.navigate(['/marketplace']);
          });
        });
      }
    });
  }


  public onSignContractClick (event: Event): void {
    this.logService.log("SIGN CONTRACT CLICKED");
    let dialogRef = this.dialogService.open(ModalDbIlvComponent, {
      data: {
        modalType: ModalDbIlvComponent.MODAL_TYPE_SIGN_CONTRACT
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.action === ModalDbIlvComponent.ACCEPT) {
        this.lastClickedActionButton.isLoading = true;
        this.projectService.updateContractState(this.projectEntity.id, Actions.SIGN_CONTRACT).subscribe((resp) => {
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_CONTRACT_SIGNED);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.lastClickedActionButton.isLoading = false;
            this.router.navigate(['/marketplace']);
          });
        });
      }
    });
  }

  public onRejectContractClick (event: Event): void {
    this.logService.log("REJECT CONTRACT CLICKED");
    let dialogRef = this.dialogService.open(ModalDbIlvComponent, {
      data: {
        modalType: ModalDbIlvComponent.MODAL_TYPE_REJECT_CONTRACT
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.action === ModalDbIlvComponent.ACCEPT) {
        this.lastClickedActionButton.isLoading = true;
        this.projectService.updateContractState(this.projectEntity.id, Actions.REJECT_CONTRACT).subscribe((resp) => {
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_CONTRACT_REJECTED);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.lastClickedActionButton.isLoading = false;
            this.router.navigate(['/marketplace']);
          });
        });
      }
    });
  }

  public onEditProgressStepClick (event: Event, progressStep: IProjectEntity): void {

    switch (progressStep.type) {
      case EntityTypes.ENTITY_TYPE_DEMAND:
        this.followRoute.emit({
          target: 'demand/' + this.route.snapshot.params['projectID'] + '/edit/' + this.projectEntity.id,
          animateToLeft: true
        });
        break;

      case EntityTypes.ENTITY_TYPE_OFFER:
        this.followRoute.emit({
          target: 'offer/' + this.route.snapshot.params['projectID'] + '/editOffer/' + this.projectEntity.id,
          animateToLeft: true
        });
        break;

      case EntityTypes.ENTITY_TYPE_CONTRACT:
        this.logService.log("TODO: EDIT MODE FOR CONTRACT");
        break;
    }
    /* if (progressStep.type == EntityTypes.ENTITY_TYPE_DEMAND) {
     this.followRoute.emit({
     target: 'demand/' + this.route.snapshot.params['projectID'] + '/edit/' + this.projectEntity.id,
     animateToLeft: true
     });
     }*/
  }

  public downloadPDF(contract: IProjectEntity){
    this.pdfService.createContractPDF(contract);
  }
}
