import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-standard-button-dbilv',
  templateUrl: './standard-button-dbilv.component.html',
  styleUrls: ['./standard-button-dbilv.component.scss']
})
export class StandardButtonDBILVComponent implements OnInit {

  @Input() title: string;
  @Input() icon: string;
  @Input() routerLink: string = '';
  @Input() buttonType: string = 'normal';
  @Input() isLoading:boolean = false;
  @Input() isEnabled:boolean = true;
  @Input() bootstrapIcon:string;

  hasIcon: string;

  constructor () {
    if (this.icon) {
      this.hasIcon = 'hasIcon';
    }
  }

  ngOnInit () {
  }

}
