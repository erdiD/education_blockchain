
import {IProgressChartDataPoint} from "../../standard-components/interfaces/IProgressChartDataPoint";

export class DataPoint implements IProgressChartDataPoint{

  id?: number;
  date: string;
  value: number;

  constructor(id = 0, date, value ) {
    this.id = id;
    this.date = date;
    this.value = value;
  }
}


