import { Pipe, PipeTransform } from '@angular/core';



interface IKeyValuePair{
  key: string;
  value: any;
}

/**
 * Transforms an Object to a List of Key Value Pairs.
 * This way it can be used inside of a "*ngFor"
 *
 * eg:
 * { "a" : "b" } => [ { "key": "a", "value": "b" }]
 *
 */
@Pipe({name: 'jsonObject'})
export class JsonObjectPipe implements PipeTransform {

  transform(jsObj: Object): IKeyValuePair[] {

    const someKindOfMap: IKeyValuePair[] = [];

    Object.getOwnPropertyNames(jsObj).forEach( (key: string, idx: number) => {
      const kvPair: IKeyValuePair = { key: key, value: jsObj[key] };
      someKindOfMap.push(kvPair);
    });

    return someKindOfMap;
  }
}
