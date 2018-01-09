import {Component, ElementRef, Input, OnInit, Output, ViewChild, EventEmitter} from '@angular/core';

@Component({
  selector: 'app-search-field-dbilv',
  templateUrl: './search-field-dbilv.component.html',
  styleUrls: ['./search-field-dbilv.component.scss']
})
export class SearchFieldDBILVComponent implements OnInit {

  @Input() name: string;
  @Input() placeholder: string;
  @Output() inputUpdate = new EventEmitter();
  @Output() confirmInput = new EventEmitter();
  @ViewChild('inputField') inputField: ElementRef;
  initialPlaceholderText: string;

  hasFocus: boolean = false;

  constructor () {
  }

  ngOnInit () {
    this.initialPlaceholderText = this.placeholder;
  }


  onFocusIn () {
    this.hasFocus = true;
    this.placeholder = '';
  }

  onFocusOut () {
    this.hasFocus = false;
    if (this.inputField.nativeElement.value === this.initialPlaceholderText || this.inputField.nativeElement.value === '') {
      this.placeholder = this.initialPlaceholderText;
    }
  }

  onInputFieldChange (event: Event) {
    let newInput = <HTMLInputElement> event.target;
    this.inputUpdate.emit(newInput.value);
  }

  onKeyUp (event: KeyboardEvent) {
    if (event.keyCode === 13) {
      let newInput = <HTMLInputElement> event.target;

      this.inputField.nativeElement.blur();
      this.confirmInput.emit(newInput.value);
    }
  }

}
