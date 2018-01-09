import {IProjectEntity} from "./IProjectEntity";

export interface IOffer extends IProjectEntity{
   demandID: string;
   price: string;
   startDate: Date;
   contractType: string;
   paymentType: string;
}
