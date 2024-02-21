import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateModule } from '@ngx-translate/core';

import { SettlementComponent } from './settlement.component';

describe('SettlementComponent', () => {
  let component: SettlementComponent;
  let fixture: ComponentFixture<SettlementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SettlementComponent],
      imports: [RouterTestingModule, TranslateModule.forRoot()],
    }).compileComponents();

    fixture = TestBed.createComponent(SettlementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
