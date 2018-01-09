import {EventEmitter, Injectable} from '@angular/core';
import {BehaviorSubject} from "rxjs/BehaviorSubject";
import {ProjectService} from "../../project/shared/project.service";

@Injectable()
export class ProjectHeaderService {

  public static HEADER_CLOSED:string = 'ProjectHeaderService.HEADER_CLOSED';
  public static HEADER_COLLAPSE:string = 'ProjectHeaderService.HEADER_COLLAPSE';

  public headerStateChanged:EventEmitter<string> = new EventEmitter();
  public currentTaskSubject = new BehaviorSubject("");

  constructor(private projectService: ProjectService) {
  }

  // public fetchProjectTask( id: string ){
  //   // this.projectService.getProject(id).subscribe( p => {
  //   //   this.currentTaskSubject.next(p.task);
  //   // });
  //   this.projectService.currentProject.subscribe( p => {
  //
  //     console.log("ProjectHeaderService.fetchProjectTask p ", p);
  //
  //     this.currentTaskSubject.next(p.task);
  //   });
  // }

  public closeHeader(){
    this.headerStateChanged.emit(ProjectHeaderService.HEADER_CLOSED);
  }

  public collapseHeader():void{
    this.headerStateChanged.emit(ProjectHeaderService.HEADER_COLLAPSE);
  }

}
