import { Pipe, PipeTransform } from '@angular/core';
import {DatePipe} from "@angular/common";


/**
 * This should enable to have a standard date / time format all over the app
 *
 * example usages:
 * const endDate = new StandardDatePipe().transform( contract["demand"].endDate );
 *
 * { endDate | stadardDate }
 * or with time:
 * { endDate | stadardDate:true }
 *
 */
@Pipe({name: 'standardDate'})
export class StandardDatePipe implements PipeTransform {

  constructor(){};

  transform(oldDate: string, withTime?: boolean): string | null {

    let newDate = null;
    if (!withTime){
      newDate = new DatePipe("de-DE").transform( oldDate, "dd.MM.yyyy");
    } else {
      newDate = new DatePipe("de-DE").transform( oldDate, "dd.MM.yyyy HH:mm:ss");
    }
    return newDate;
  }

}
