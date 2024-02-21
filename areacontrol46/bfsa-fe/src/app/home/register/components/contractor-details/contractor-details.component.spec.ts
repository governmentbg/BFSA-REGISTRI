import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { ContractorDetailsComponent } from './contractor-details.component';

describe('ContractorDetailsComponent', () => {
  let component: ContractorDetailsComponent;
  let fixture: ComponentFixture<ContractorDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ContractorDetailsComponent],
      imports: [
        NoopAnimationsModule,
        HttpClientModule,
        RouterModule.forRoot([]),
        TranslateModule.forRoot(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ContractorDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
