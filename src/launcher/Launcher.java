package launcher;

import model.Model;
import view.View;
import controller.Controller;

public class Launcher {

	public static void main(String[] args) {
		if(args.length != 1) { System.out.println("vous avez oublié de spécifier le port !"); }
		int port = Integer.parseInt(args[0]);
		Model model = new Model();
		Controller controller = new Controller(model, port);
		Debug.get().setModel(model);
		new View(model, controller);
		
		
		//La vue doit connaître le contrôleur pour lui indiquer les évènements dûs à l'utilisateur
		/*View view = */
		//Le contrôleur doit écouter/observer la vue pour la prévenir des changements.
		//Le contrôleur doit connaître le modèle pour le mettre à jour

	}

}
