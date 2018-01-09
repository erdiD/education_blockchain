import {Pipe} from '@angular/core'
import * as mimeTypes from "mime-types";

@Pipe({
  name: 'fileName'
})
export class FileNamePipe {

  transform(fileTypeString: string) : string {

    const fileType: string = mimeTypes.extension(fileTypeString);
    if (!fileType) {
      return "file"
    }

    return fileType;
  }
}
