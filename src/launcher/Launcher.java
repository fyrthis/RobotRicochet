package launcher;

import controller.Controller;
import model.Model;
import view.View;

public class Launcher {

	public static void main(String[] args) {
		Model model = new Model();
		
		Controller controller = new Controller(model);
		Debug.get().setModel(model);
		new View(model, controller);
		
		
		//La vue doit connaître le contrôleur pour lui indiquer les évènements dûs à l'utilisateur
		/*View view = */
		//Le contrôleur doit écouter/observer la vue pour la prévenir des changements.
		//Le contrôleur doit connaître le modèle pour le mettre à jour

	}

}
