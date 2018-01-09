import {EventEmitter, Injectable, OnInit} from '@angular/core';

@Injectable()
export class BackgroundService implements OnInit {

  public stateChangeEmitter: EventEmitter<string> = new EventEmitter();

  public static BACKGROUND_STATE_IDLE: string = 'BACKGROUND_STATE_IDLE';
  public static BACKGROUND_STATE_FLASH: string = 'BACKGROUND_STATE_FLASH';

  constructor () {
  }

  public ngOnInit () {
  }

  public changeBackgroundState (state: string): void {
    switch (state) {
      case BackgroundService.BACKGROUND_STATE_IDLE:
        this.stateChangeEmitter.emit(BackgroundService.BACKGROUND_STATE_IDLE);
        break;
      case BackgroundService.BACKGROUND_STATE_FLASH:
        this.stateChangeEmitter.emit(BackgroundService.BACKGROUND_STATE_FLASH);
        break;
    }
  }
}
