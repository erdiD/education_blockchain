import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'sort'
})
export class ArraySortPipe implements PipeTransform {
  transform(arr: Array<any>, prop: any, reverse: boolean = false): any {
    if (arr === undefined) return
    const m = reverse ? -1 : 1
    return arr.sort((a: any, b: any): number => {
      let x = a[prop];
      let y = b[prop];

      if(typeof(x) === 'string'){
        x = x.toLowerCase();
        y = y.toLowerCase();
      }

      if(prop === 'budget'){
        x = parseInt(x);
        y = parseInt(y);
      }



      return (x === y) ? 0 : (x < y) ? -1*m : 1*m
    })
  }
}
