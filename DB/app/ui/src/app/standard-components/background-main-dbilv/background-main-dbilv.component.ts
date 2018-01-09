import {Component, OnInit, ViewChild} from '@angular/core';
import {ElementRef, Renderer2} from '@angular/core';
import {IntervalObservable} from 'rxjs/observable/IntervalObservable';
import {BackgroundService} from "../services/background.service";

@Component({
  selector: 'app-background-main-dbilv',
  templateUrl: './background-main-dbilv.component.html',
  styleUrls: ['./background-main-dbilv.component.scss']
})
export class BackgroundMainDBILVComponent implements OnInit {

  @ViewChild('pol1') pol1: ElementRef;
  @ViewChild('pol2') pol2: ElementRef;
  @ViewChild('pol3') pol3: ElementRef;
  @ViewChild('pol4') pol4: ElementRef;

  polygons: Polygon[];
  isInFlashMode = false;

  constructor (private renderer: Renderer2, private backgroundService: BackgroundService) {
  }

  ngOnInit () {
    this.polygons = [
      {
        element: this.pol1.nativeElement,
        left: '0px',
        top: '0px',
        scale: '1',
        opacity: 1
      }, {
        element: this.pol2.nativeElement,
        left: '0px',
        top: '0px',
        scale: '1',
        opacity: 1
      }, {
        element: this.pol3.nativeElement,
        left: '0px',
        top: '0px',
        scale: '1',
        opacity: 1
      },
      {
        element: this.pol4.nativeElement,
        left: '0px',
        top: '0px',
        scale: '1',
        opacity: 1
      },
      {
        element: this.pol4.nativeElement,
        left: '0px',
        top: '0px',
        scale: '1',
        opacity: 1
      },
      {
        element: this.pol4.nativeElement,
        left: '0px',
        top: '0px',
        scale: '1',
        opacity: 1
      }
    ];

    this.setNewPositions();

    setTimeout(() => {
      this.setNewPositions();
    }, 200);

    IntervalObservable.create(20000).subscribe(() => {
      if (!this.isInFlashMode) {
        this.setNewPositions();
      }
    });

    /*setTimeout(() => {
     this.flashBackground();
     }, 5000);*/

    this.backgroundService.stateChangeEmitter.subscribe((state:string) => {
      if(state === BackgroundService.BACKGROUND_STATE_FLASH){
        this.flashBackground();
      }else if(state === BackgroundService.BACKGROUND_STATE_IDLE){
        this.endFlashAndSpreadOut();
      }
    });

  }

  flashBackground (): void {
    this.isInFlashMode = true;
    for (let i = 0; i < this.polygons.length; i++) {
      this.setNewPositionForPolygon(this.polygons[i], 50 + (Math.random() * 6 - 3), 50 + (Math.random() * 4 - 2),
        Math.random() * 1 + 2, Math.random() * 0.9 + 0.1);
    }
  }

  endFlashAndSpreadOut(){
    // spraed out again
    setTimeout(() => {
      this.setNewPositions();
    }, 0);

    // start slow wiggle-cycle
    setTimeout(() => {
      this.isInFlashMode = false;
      this.setNewPositions();
    }, 1500);
  }

  setNewPositions (): void {
    for (let i = 0; i < this.polygons.length; i++) {
      this.setNewPositionForPolygon(
        this.polygons[i],
        50 + (Math.random() * 50 - 25), 50 + (Math.random() * 12 - 6), Math.random() * 2 + 0.3, Math.random() * 0.9 + 0.1);
    }
  }

  setNewPositionForPolygon (polygon: Polygon, newX: number, newY: number, newScale: number, newOpacity: number): void {
    polygon.left = newX + "%";
    polygon.top = newY + "%";
    polygon.scale = 'scale(' + newScale + ')';
    polygon.opacity = newOpacity;
  }

}

interface Polygon {
  element: any;
  left: string;
  top: string;
  scale: string;
  opacity: number;
}
