import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule } from '@angular/material/snack-bar';

import { ActivityGroupListComponent } from './activity-group-list.component';

describe('ActivityGroupListComponent', () => {
  let component: ActivityGroupListComponent;
  let fixture: ComponentFixture<ActivityGroupListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ActivityGroupListComponent],
      imports: [
        HttpClientModule,
        MatDialogModule,
        MatIconModule,
        MatSnackBarModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ActivityGroupListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
