
import {IProgressChartDataPoint} from "../../standard-components/interfaces/IProgressChartDataPoint";
import {DataPoint} from "./stats-datapoint.model";


export class Stats {

  public demandId: string;
  public budget: number;
  public duration: number;
  public totalPaid: number;
  public totalProgress: number;

  public paymentsPerMonth: Map<string, number> = new Map<string,  number>();

  constructor( id: string = "" ){
    this.demandId = id;
  };

  public static fromJSON (data: any): Stats {
    let stats = new Stats();
    stats.demandId = data.id || "";
    stats.budget = data.budget;
    stats.duration = data.duration;
    stats.totalPaid = data.totalPaid;
    stats.totalProgress = data.totalProgress || 0;
    stats.paymentsPerMonth = data.paymentsPerMonth;

    return stats;
  }

  public static fromJsonWithId (data: any, demandId: string): Stats {
    let stats = this.fromJSON(data);
    stats.demandId = demandId;
    return stats;
  }


  public getProgressChartsDataPoints(): Array<IProgressChartDataPoint>{
    const chartArray = [];

    Object.getOwnPropertyNames(this.paymentsPerMonth).sort().forEach( (key: string, idx: number) => {
      chartArray.push(new DataPoint(idx, key, this.paymentsPerMonth[key]));
    });

    return chartArray;
  }

}
