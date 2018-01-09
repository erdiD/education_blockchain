import {Pipe} from '@angular/core'
import * as mimeTypes from "mime-types";

@Pipe({
  name: 'commaRemoval'
})
export class CommaRemovalPipe {

  transform( commaNumber: string) : string {

    const idx = commaNumber.lastIndexOf(",");
    if (idx > 0) {
      return commaNumber.slice(0, idx);
    }

    return commaNumber;
  }



}
