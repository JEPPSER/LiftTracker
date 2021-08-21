import { formatDate } from "@angular/common";
import { Component, ElementRef, Inject, Input, LOCALE_ID, ViewChild } from "@angular/core";
import { Platform } from "@ionic/angular";

@Component({
	selector: 'scatter-plot',
	templateUrl: 'scatter-plot.component.html'
})
export class ScatterPlotComponent {

	@Input() data: PlotPoint[];
	@Input() drawLine: boolean = true;
	@Input() propDates: boolean = false;

	@ViewChild('canvas') canvasEl: ElementRef;

	canvas;
	context;

	xAxis: Axis;
	yAxis: Axis;

	private readonly PADDING = 50;

	constructor(
		private platform: Platform,
		@Inject(LOCALE_ID) private locale: string
	) {}

	ngAfterViewInit() {
		this.canvas = this.canvasEl.nativeElement;
		this.canvas.width = this.platform.width();
		this.canvas.height = this.platform.width();
		this.context = this.canvas.getContext('2d');
		this.draw();
	}

	draw() {
		if (!this.data || this.data.length == 0) {
			return;
		}

		this.data = this.sortData();
		this.xAxis = new Axis(this.data, 1, false);
		this.yAxis = new Axis(this.data, 0, true);

		this.context.clearRect(0, 0, this.canvas.width, this.canvas.height);
		let width = this.canvas.width - this.PADDING * 2;
		//this.context.fillStyle = "#000";
		//this.context.fillRect(0, 0, this.canvas.width, this.canvas.width);
		this.context.fillStyle = "#000";
		this.context.fillRect(this.PADDING, this.PADDING, 5, width);
		this.context.fillRect(this.PADDING, width + this.PADDING, width, 5);

		if (this.xAxis && this.yAxis) {
			let xInc = width / this.xAxis.numOfTicks;
			let yInc = width / this.yAxis.numOfTicks;

			if (this.propDates) {
				this.context.fillRect(this.PADDING, width + this.PADDING, 5, 17);
				this.context.strokeText(formatDate(this.data[0].date, 'MMM dd', this.locale), this.PADDING - 10, width + 80);
				this.context.fillRect(width + this.PADDING, width + this.PADDING, 5, 17);
				this.context.strokeText(formatDate(this.data[this.data.length - 1].date, 'MMM dd', this.locale), width + this.PADDING - 10, width + 80);
			} else {
				for (let i = 1; i < this.xAxis.numOfTicks + 1; i++) {
					let x = this.PADDING + i * xInc;
					this.context.fillStyle = '#777';
					this.context.fillRect(x, this.PADDING, 1, width);
					this.context.fillStyle = '#000';
					this.context.fillRect(x - 2, width + this.PADDING - 10, 5, 25);
					if (i < this.xAxis.numOfTicks) {
						let index = (i - 1) * this.xAxis.tickSkip;
						this.context.strokeText(formatDate(this.data[index].date, 'MMM dd', this.locale), x - 10, width + 80);
					}
				}
			}

			for (let i = 1; i < this.yAxis.numOfTicks + 1; i++) {
				let y = width + this.PADDING - i * yInc;
				this.context.fillStyle = '#777';
				this.context.fillRect(this.PADDING, y, width, 1);
				this.context.fillStyle = '#000';
				this.context.fillRect(this.PADDING - 10, y - 2, 25, 5);
				this.context.strokeText(this.yAxis.start + i * this.yAxis.tickSpacing, 10, y + 5);
			}

			let xScale = xInc / this.xAxis.tickSpacing;
			let yScale = yInc / this.yAxis.tickSpacing;

			let prev;
			let last: Date = this.data[this.data.length - 1].date;
			let dif = this.daysBetween(this.data[0].date, last);
			let scale = width / dif;

			for (let p of this.data) {
				let pointX;
				if (this.propDates) {
					pointX = width + this.PADDING - this.daysBetween(p.date, last) * scale;
				} else {
					pointX = (this.PADDING + (this.data.indexOf(p) - this.xAxis.start) * xScale + xInc);
				}
				let pointY = (width + this.PADDING - (p.value - this.yAxis.start) * yScale);

				if (!prev) {
					prev = { x: pointX, y: pointY };
				} else if (this.drawLine) {
					this.context.beginPath();
					this.context.moveTo(prev.x, prev.y);
					this.context.lineTo(pointX, pointY);
					this.context.stroke();
					prev = { x: pointX, y: pointY };
				}

				this.context.beginPath();
				this.context.arc(pointX, pointY, 5, 0, 2 * Math.PI, false);
				this.context.fill();
			}
		}
	}

	daysBetween(start, end) {
		const date1 = new Date(start);
		const date2 = new Date(end);

		// One day in milliseconds
		const oneDay = 1000 * 60 * 60 * 24;

		// Calculating the time difference between two dates
		const diffInTime = date2.getTime() - date1.getTime();

		// Calculating the no. of days between two dates
		const diffInDays = Math.round(diffInTime / oneDay);

		return diffInDays;
	}

	sortData() {
		let items = this.data.sort((a: any, b: any) =>
        	new Date(a.date).getTime() - new Date(b.date).getTime()
    	);
		return items;
	}
}

export interface PlotPoint {
	date: Date;
	value: number;
}

export class Axis {

	readonly VERTICAL: number = 0;
	readonly HORIZONTAL: number = 1;

	min: number;
	max: number;
	start: number;
	tickSpacing: number;
	numOfTicks: number;
	tickSkip: number;
	orientation: number;

	constructor(data: PlotPoint[], orientation: number, fromZero: boolean) {
		this.orientation = orientation;

		if (!data || data.length == 0) {
			return;
		}

		if (this.orientation == this.VERTICAL) {
			this.min = data[0].value;
			this.max = data[0].value;
		} else if (this.orientation == this.HORIZONTAL) {
			this.min = 0;
			this.max = 0;
		}

		for (let p of data) {
			if (this.orientation == this.VERTICAL) {
				if (p.value < this.min) {
					this.min = p.value;
				}
				if (p.value > this.max) {
					this.max = p.value;
				}
			} else if (this.orientation == this.HORIZONTAL) {
				this.min = 0;
				this.max = data.length - 1;
			}
		}

		if (fromZero) {
			this.min = 0;
		}

		let dif = this.max - this.min;
		let it = 1.0;
		let log = Math.floor(Math.log10(dif));

		if (this.orientation == this.VERTICAL) {
			this.tickSpacing = Math.pow(10, log);
			this.numOfTicks = this.getSignificantDigit(dif);
			if (this.numOfTicks < 5) {
				it = 0.5;
				this.numOfTicks *= 2;
			}
			this.numOfTicks += 2;
		} else if (this.orientation == this.HORIZONTAL) {
			this.tickSpacing = 1;
			this.numOfTicks = data.length;
			this.tickSkip = 1;
			while (this.numOfTicks > 5) {
				this.tickSkip *= 2;
				it *= 2;
				this.numOfTicks = Math.ceil(this.numOfTicks / 2);
			}
			this.numOfTicks += 1;
		}

		this.tickSpacing *= it;
		this.start = Math.floor(this.min / this.tickSpacing) * this.tickSpacing;
	}

	private getSignificantDigit(number) {
		let str = number.toString();
		for (let i = 0; i < str.length; i++) {
			if (str.charAt(i) != '0' && str.charAt(i) != '.') {
				return parseInt('' + str.charAt(i));
			}
		}
	}
}
