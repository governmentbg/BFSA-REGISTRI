import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule } from '@angular/router';
import { ContractorVehiclesTableComponent } from './components/contractor-vehicles-table/contractor-vehicles-table.component';
import { RegisterSearchComponent } from './components/register-search/register-search.component';
import { RegisterPageComponent } from './register-page/register-page.component';
import { PermissionsComponentTable } from './components/permissions-table/permissions-table.component';
import { ContractorPppFacilitiesTableComponent } from './components/contractor-ppp-facilities-table/contractor-ppp-facilities-table.component';
import { ContractorPlantProtectionServicesDialogComponent } from './components/contractor-plant-protection-services-dialog/contractor-plant-protection-services-dialog.component';
import { ContractorDetailsComponent } from './components/contractor-details/contractor-details.component';
import { ContractorCertificates83TableComponent } from './components/contractor-certificates83-table/contractor-certificates83-table.component';
import { MatTabsModule } from '@angular/material/tabs';
import { ContractorFacilitiesTableComponent } from './components/contractor-facilities-table/contractor-facilities-table.component';
import { TranslateModule } from '@ngx-translate/core';
import { ContractorDistanceTradingTableComponent } from './components/contractor-distance-trading-table/contractor-distance-trading-table.component';
import { AdjuvantTableComponent } from './components/adjuvant-table/adjuvant-table.component';
import { ContractorPlantProtectionServicesTableComponent } from './components/contractor-plant-protection-services-table/contractor-plant-protection-services-table.component';
import { ContractorFoodAdditivesTableComponent } from './components/contractor-food-additives-table/contractor-food-additives-table.component';
import { PermissionsDialogComponent } from './components/permissions-dialog/permissions-dialog.component';
import { ContractorVehiclesDialogComponent } from './components/contractor-vehicles-dialog/contractor-vehicles-dialog.component';
import { ContractorFoodAdditivesDialogComponent } from './components/contractor-food-additives-dialog/contractor-food-additives-dialog.component';
import { ViewS3180DialogComponent } from './components/view-s3180-dialog/view-s3180-dialog.component';
import { ContractorDistanceTradingDialogComponent } from './components/contractor-distance-trading-dialog/contractor-distance-trading-dialog.component';
import { ContractorCertificates83DialogComponent } from './components/contractor-certificates83-dialog/contractor-certificates83-dialog.component';
import { RegisterRoutingModule } from './register-routing.module';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { HomeModule } from '../home.module';
import { ViewS3182DialogComponent } from './components/view-s3182-dialog/view-s3182-dialog.component';
import { ContractorFertilizersTableComponent } from './components/contractor-fertilizers-table/contractor-fertilizers-table.component';
import { ContractorFertilizersDialogComponent } from './components/contractor-fertilizers-dialog/contractor-fertilizers-dialog.component';
import { ContractorSeedsTableComponent } from './components/contractor-seeds-table/contractor-seeds-table.component';
import { ContractorSeedsDialogComponent } from './components/contractor-seeds-dialog/contractor-seeds-dialog.component';
import { TenantSalesmanTableComponent } from './components/tenant-salesman-table/tenant-salesman-table.component';

@NgModule({
  declarations: [
    ContractorVehiclesTableComponent,
    RegisterSearchComponent,
    RegisterPageComponent,
    PermissionsComponentTable,
    ContractorPppFacilitiesTableComponent,
    ContractorPlantProtectionServicesDialogComponent,
    ContractorDetailsComponent,
    ContractorCertificates83TableComponent,
    ContractorFacilitiesTableComponent,
    ContractorDistanceTradingTableComponent,
    AdjuvantTableComponent,
    ContractorPlantProtectionServicesTableComponent,
    ContractorFoodAdditivesTableComponent,
    PermissionsDialogComponent,
    ContractorVehiclesDialogComponent,
    ContractorFoodAdditivesDialogComponent,
    ViewS3180DialogComponent,
    ContractorDistanceTradingDialogComponent,
    ContractorCertificates83DialogComponent,
    ViewS3182DialogComponent,
    ContractorFertilizersTableComponent,
    ContractorFertilizersDialogComponent,
    ContractorSeedsTableComponent,
    ContractorSeedsDialogComponent,
    TenantSalesmanTableComponent,
  ],
  exports: [RegisterSearchComponent],
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatTooltipModule,
    MatTableModule,
    MatPaginatorModule,
    MatSelectModule,
    MatDialogModule,
    MatAutocompleteModule,
    RouterModule,
    MatTabsModule,
    TranslateModule,
    MatDialogModule,
    RegisterRoutingModule,
    MatCheckboxModule,
    HomeModule,
  ],
})
export class RegisterModule {}
