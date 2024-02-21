import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule } from '@angular/material/snack-bar';

import { ClassifierListComponent } from './classifier-list.component';

describe('ClassifierListComponent', () => {
  let component: ClassifierListComponent;
  let fixture: ComponentFixture<ClassifierListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ClassifierListComponent],
      imports: [
        HttpClientModule,
        MatDialogModule,
        MatIconModule,
        MatSnackBarModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ClassifierListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
