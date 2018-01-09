import {ApplicationRef, ChangeDetectorRef, Component, Input, OnInit, Output, ViewChild} from '@angular/core';
import {IProgressChartDataPoint} from "../interfaces/IProgressChartDataPoint";
import {Stats} from "../../shared/models/stats.model";
import {StatsService} from "../../project/project-stats/stats.service";
import {nvD3} from "ng2-nvd3";
import {isNullOrUndefined, isUndefined} from "util";

@Component({
  selector: 'app-progress-chart',
  templateUrl: './progress-chart.component.html',
  styleUrls: ['./progress-chart.component.scss']
})
export class ProgressChartComponent implements OnInit {


  @Input() dataPoints:IProgressChartDataPoint[] = [];

  public chartOptions:any;

  public chartData:any;

  @ViewChild("barChart") barChart: nvD3;

  constructor ( ) {

    const values = this.dataPoints;
    this.chartData = [
      {
        key: "Wert",
        values: values
      }
    ];

    this.chartOptions = {
      chart: {
        type: 'discreteBarChart',
        useInteractiveGuideline: false,
        duration: 1500,
        color:['#28d7ff'],
        height: 330,
        margin : {
          top: 110,
          right: 25,
          bottom: 60,
          left: 25
        },
        x: function(d){return d.date;},
        y: function(d){ return Number(d.value);},
        valueFormat: function(d){
          return d3.format(',.4f')(d);
        },
        showValues: false,
        showXAxis:true,
        showYAxis:false,
        staggerLabels: false,
        reduceXTicks: true,
        xAxis: {
          showMaxMin: false,
          rotateLabels: 30,
          tickFormat: function(data, idx){
            if (idx % 2 == 0){
              return data;
            }
            return "";
          }
        },
        yAxis: {
          axisLabel: ' ',
          axisLabelDistance: 40
        },
         tooltip: {
          "duration": 0,
          "gravity": "w",
          "distance": 25,
          "snapDistance": 0,
          "classes": ["barChartToolTip"],
          "chartContainer": null,
          "enabled": true,
          "hideDelay": 200,
          "headerEnabled": false,
          "fixedTop": null,
          "offset": {
            "left": 0,
            "top": 0
          },
          "hidden": false,
          "data": null,
          id: "progressChartTooltip"
        }
      }
    };

    // tooltip: {
    //   keyFormatter: function (d) {
    //     return d;
    //   }
    // },

  }

  public ngOnInit (): void {
  }

  public updateBarChart( dataPoints: IProgressChartDataPoint[] = this.dataPoints){
    this.dataPoints = dataPoints;
  }

  public ngOnChanges( changed ) {

    if (changed.dataPoints && changed.dataPoints.currentValue.length > 0){
      Promise.resolve().then( () => {
        const values = [
          {
            key: "Wert",
            values: []
          }
        ];
        this.chartData[0].values = [];

        changed.dataPoints.currentValue.map( dp => {
          this.chartData[0].values.push(
            <IProgressChartDataPoint>{
            date: dp.date,
            value: dp.value
          });
        } );
      }).then( () => {
        this.barChart.update();
      });
    }
  }

}

