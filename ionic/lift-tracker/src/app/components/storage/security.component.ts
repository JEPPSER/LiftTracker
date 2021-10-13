import { Component } from "@angular/core";
import { Router } from "@angular/router";
import { ToastController } from "@ionic/angular";

@Component({
	selector: 'security',
	templateUrl: 'security.component.html'
})
export class SecurityComponent {

	key: string;

	constructor(
		public router: Router,
		private toastController: ToastController
	) {}

	ngOnInit() {
		this.key = localStorage.getItem('pasteKey');
	}

	update() {
		localStorage.setItem('pasteKey', this.key);
		this.presentToast('Nyckeln har uppdaterats', 'success');
	}

	async presentToast(text: string, color: string) {
		const toast = await this.toastController.create({
			message: text,
			color: color,
			duration: 2000
		});
		await toast.present();
	}
}
