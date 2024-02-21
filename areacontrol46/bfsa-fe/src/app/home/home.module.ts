import { CommonModule, DatePipe } from '@angular/common';
import { NgModule } from '@angular/core';
import { LayoutModule } from '@angular/cdk/layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatStepperModule } from '@angular/material/stepper';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatTreeModule } from '@angular/material/tree';
import { TranslateModule } from '@ngx-translate/core';
import { NavComponent } from './components/nav/nav.component';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';
import { TasksComponent } from './components/tasks/tasks.component';
import { HomeRoutingModule } from './home-routing.module';
import { HomeComponent } from './home.component';
import { ApplicationsComponent } from './components/applications/applications.component';
import { MatRadioModule } from '@angular/material/radio';
import { SanitizeHtmlPipe } from '../pipes/sanitizeHtmlPipe';
import { FoodTypesCheckboxListComponent } from './components/applications/food-types-checkbox-list/food-types-checkbox-list.component';
import { EditApplicationS3180Component } from './components/applications/edit-application-s3180/edit-application-s3180.component';
import { NestedFoodTypesListComponent } from './components/applications/nested-food-types-list/nested-food-types-list.component';
import { RefusalDialogComponent } from './components/applications/refusal-dialog/refusal-dialog.component';
import { DirectRegisterDialogComponent } from './components/applications/direct-register-dialog/direct-register-dialog.component';
import { ApprovalDialogComponent } from './components/applications/approval-dialog/approval-dialog.component';
import { PrintDocumentDialogComponent } from './components/applications/print-document-dialog/print-document-dialog.component';
import { DataAndDownloadDialogComponent } from './components/applications/data-and-download-dialog/data-and-download-dialog.component';
import { IrregularitiesDialogComponent } from './components/applications/irregularities-dialog/irregularities-dialog.component';
import { CreateApplicationS2701Component } from './components/applications/create-application-s2701/create-application-s2701.component';
import { EditApplicationS2701Component } from './components/applications/edit-application-s2701/edit-application-s2701.component';
import { DiscrepancyDialogComponent } from './discrepancy-dialog/discrepancy-dialog.component';
import { ExpertModalComponent } from './components/applications/expert-modal/expert-modal.component';
import { FinanceModalComponent } from './components/applications/finance-modal/finance-modal.component';
import { CreateApplicationS3180Component } from './components/applications/create-application-s3180/create-application-s3180.component';
import { CreateApplicationS3181Component } from './components/applications/create-application-s3181/create-application-s3181.component';
import { EditApplicationS3181Component } from './components/applications/edit-application-s3181/edit-application-s3181.component';
import { ForCorrectionDialogComponent } from './components/applications/for-correction-dialog/for-correction-dialog.component';
import { ConfirmDialogComponent } from './components/applications/confirm-dialog/confirm-dialog.component';
import { EditApplicationS3182Component } from './components/applications/edit-application-s3182/edit-application-s3182.component';
import { CreateApplicationS3182Component } from './components/applications/create-application-s3182/create-application-s3182.component';
import { CreateApplicationS1590Component } from './components/applications/create-application-s1590/create-application-s1590.component';
import { EditApplicationS1590Component } from './components/applications/edit-application-s1590/edit-application-s1590.component';
import { CreateApplicationS2699Component } from './components/applications/create-application-s2699/create-application-s2699.component';
import { EditApplicationS2699Component } from './components/applications/edit-application-s2699/edit-application-s2699.component';
import { CreateApplicationS2272Component } from './components/applications/create-application-s2272/create-application-s2272.component';
import { EditApplicationS2272Component } from './components/applications/edit-application-s2272/edit-application-s2272.component';
import { CreateApplicationS2702Component } from './components/applications/create-application-s2702/create-application-s2702.component';
import { EditApplicationS2702Component } from './components/applications/edit-application-s2702/edit-application-s2702.component';
import { ErrorsDialogComponent } from './components/applications/errors-dialog/errors-dialog.component';
import { EditApplicationS1199Component } from './components/applications/edit-application-s1199/edit-application-s1199.component';
import { EditApplicationS1811Component } from './components/applications/edit-application-s1811/edit-application-s1811.component';
import { EditApplicationS503Component } from './components/applications/edit-application-s503/edit-application-s503.component';
import { EditApplicationS2869Component } from './components/applications/edit-application-s2869/edit-application-s2869.component';
import { EditApplicationS3125Component } from './components/applications/edit-application-s3125/edit-application-s3125.component';
import { EditApplicationS2700Component } from './components/applications/edit-application-s2700/edit-application-s2700.component';
import { ViewConsultantsDialogComponent } from './register/components/view-consultants-dialog/view-consultants-dialog.component';
import { ViewAttachmentsDialogComponent } from './components/applications/view-attachments-dialog/view-attachments-dialog.component';
import { EditApplicationS2698Component } from './components/applications/edit-application-s2698/edit-application-s2698.component';
import { EditApplicationS502Component } from './components/applications/edit-application-s502/edit-application-s502.component';
import { PaymentConfirmDialogComponent } from './components/applications/payment-confirm-dialog/payment-confirm-dialog.component';
import { ApprovalOrderDateDialogComponent } from './components/applications/approval-order-date-dialog/approval-order-date-dialog.component';
import { AdjuvantDialogComponent } from './register/components/adjuvant-dialog/adjuvant-dialog.component';
import { DataDialogComponent } from './components/applications/data-dialog/data-dialog.component';
import { EditApplicationS2711Component } from './components/applications/edit-application-s2711/edit-application-s2711.component';
import { EditApplicationS7694Component } from './components/applications/edit-application-s7694/edit-application-s7694.component';
import { EditApplicationS2697Component } from './components/applications/edit-application-s2697/edit-application-s2697.component';
import { EditApplicationS7693Component } from './components/applications/edit-application-s7693/edit-application-s7693.component';
import { EditApplicationS7695Component } from './components/applications/edit-application-s7695/edit-application-s7695.component';
import { ApplicationCommonPartComponent } from './components/applications/application-common-part/application-common-part.component';
import { EditApplicationS2695Component } from './components/applications/edit-application-s2695/edit-application-s2695.component';
import { EditApplicationS3201Component } from './components/applications/edit-application-s3201/edit-application-s3201.component';
import { EditApplicationS2170Component } from './components/applications/edit-application-s2170/edit-application-s2170.component';
import { EditApplicationS3362Component } from './components/applications/edit-application-s3362/edit-application-s3362.component';
import { EditApplicationS3363Component } from './components/applications/edit-application-s3363/edit-application-s3363.component';
import { EditApplicationS3365Component } from './components/applications/edit-application-s3365/edit-application-s3365.component';
import { EditApplicationS13661Component } from './components/applications/edit-application-s13661/edit-application-s13661.component';
import { EditApplicationS13662Component } from './components/applications/edit-application-s13662/edit-application-s13662.component';
import { EditApplicationS7692Component } from './components/applications/edit-application-s7692/edit-application-s7692.component';
import { EditApplicationS7691Component } from './components/applications/edit-application-s7691/edit-application-s7691.component';
import { EditApplicationS7696Component } from './components/applications/edit-application-s7696/edit-application-s7696.component';
import { EditApplicationS2274Component } from './components/applications/edit-application-s2274/edit-application-s2274.component';

@NgModule({
  declarations: [
    HomeComponent,
    NavComponent,
    PageNotFoundComponent,
    TasksComponent,
    ApplicationsComponent,
    CreateApplicationS3180Component,
    EditApplicationS3180Component,
    SanitizeHtmlPipe,
    FoodTypesCheckboxListComponent,
    NestedFoodTypesListComponent,
    RefusalDialogComponent,
    DirectRegisterDialogComponent,
    ApprovalDialogComponent,
    PrintDocumentDialogComponent,
    DataAndDownloadDialogComponent,
    IrregularitiesDialogComponent,
    CreateApplicationS2701Component,
    EditApplicationS2701Component,
    DiscrepancyDialogComponent,
    ExpertModalComponent,
    FinanceModalComponent,
    CreateApplicationS3181Component,
    EditApplicationS3181Component,
    ForCorrectionDialogComponent,
    ConfirmDialogComponent,
    EditApplicationS3182Component,
    CreateApplicationS3182Component,
    CreateApplicationS1590Component,
    EditApplicationS1590Component,
    CreateApplicationS2699Component,
    EditApplicationS2699Component,
    CreateApplicationS2272Component,
    EditApplicationS2272Component,
    CreateApplicationS2702Component,
    EditApplicationS2702Component,
    ErrorsDialogComponent,
    EditApplicationS1199Component,
    EditApplicationS1811Component,
    EditApplicationS503Component,
    EditApplicationS2869Component,
    EditApplicationS3125Component,
    EditApplicationS2700Component,
    ViewConsultantsDialogComponent,
    ViewAttachmentsDialogComponent,
    EditApplicationS2698Component,
    EditApplicationS502Component,
    PaymentConfirmDialogComponent,
    ApprovalOrderDateDialogComponent,
    AdjuvantDialogComponent,
    DataDialogComponent,
    EditApplicationS2711Component,
    EditApplicationS7694Component,
    EditApplicationS2697Component,
    EditApplicationS7693Component,
    EditApplicationS7695Component,
    ApplicationCommonPartComponent,
    EditApplicationS2695Component,
    EditApplicationS3201Component,
    EditApplicationS2170Component,
    EditApplicationS3362Component,
    EditApplicationS3363Component,
    EditApplicationS3365Component,
    EditApplicationS13661Component,
    EditApplicationS13662Component,
    EditApplicationS7692Component,
    EditApplicationS7691Component,
    EditApplicationS7696Component,
    EditApplicationS2274Component,
  ],
  imports: [
    CommonModule,
    HomeRoutingModule,
    MatGridListModule,
    MatRadioModule,
    MatCardModule,
    MatMenuModule,
    MatIconModule,
    MatStepperModule,
    MatButtonModule,
    LayoutModule,
    MatToolbarModule,
    MatSidenavModule,
    MatFormFieldModule,
    MatListModule,
    MatExpansionModule,
    MatTabsModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatInputModule,
    MatCheckboxModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    ReactiveFormsModule,
    MatTooltipModule,
    MatSlideToggleModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    TranslateModule,
    MatTreeModule,
  ],
  providers: [DatePipe], // Include DatePipe in the providers array
  exports: [SanitizeHtmlPipe],
})
export class HomeModule {}
