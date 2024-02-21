import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { ViewS3180DialogComponent } from './view-s3180-dialog.component';

describe('ViewS3180DialogComponent', () => {
  let component: ViewS3180DialogComponent;
  let fixture: ComponentFixture<ViewS3180DialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ViewS3180DialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      imports: [NoopAnimationsModule, HttpClientModule],
    }).compileComponents();

    fixture = TestBed.createComponent(ViewS3180DialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
