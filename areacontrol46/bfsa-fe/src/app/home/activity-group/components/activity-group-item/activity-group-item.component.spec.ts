import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivityGroupItemComponent } from './activity-group-item.component';

describe('ActivityGroupItemComponent', () => {
  let component: ActivityGroupItemComponent;
  let fixture: ComponentFixture<ActivityGroupItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ActivityGroupItemComponent],
      imports: [
        HttpClientModule,
        RouterTestingModule,
        MatSnackBarModule,
        MatDialogModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ActivityGroupItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
