import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewS3182DialogComponent } from './view-s3182-dialog.component';
import { HttpClientModule } from '@angular/common/http';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('ViewS3182DialogComponent', () => {
  let component: ViewS3182DialogComponent;
  let fixture: ComponentFixture<ViewS3182DialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ViewS3182DialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      imports: [NoopAnimationsModule, HttpClientModule],
    }).compileComponents();

    fixture = TestBed.createComponent(ViewS3182DialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
