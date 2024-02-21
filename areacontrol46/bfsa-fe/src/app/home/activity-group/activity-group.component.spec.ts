import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateModule } from '@ngx-translate/core';

import { ActivityGroupComponent } from './activity-group.component';

describe('ActivityGroupComponent', () => {
  let component: ActivityGroupComponent;
  let fixture: ComponentFixture<ActivityGroupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ActivityGroupComponent],
      imports: [RouterTestingModule, TranslateModule.forRoot()],
    }).compileComponents();

    fixture = TestBed.createComponent(ActivityGroupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
