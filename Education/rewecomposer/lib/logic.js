'use strict';
/**
 * Write your transction processor functions here
 */

/**
 * Route und Status des Produktes
 * @param {org.example.rewe.Route} route - the route to be processed
 * @transaction
 */
function sendTo(route) {
    var b = route.bag
    var s = route.status
    var m = route.tmem;
    if(b.route == null){
        b.route = [];
    }
    b.route.push(route); //Route der Ware wird hinzugefügt
    b.status = s; //Status wird geändert.
  	b.owner = m; // Update Member
    return getAssetRegistry('org.example.rewe.Bag')
       .then(function(BagRegistry){
        	return BagRegistry.update(b);
        });
}

/**
 * Zertifizierungen des Produktes
 * @param {org.example.rewe.Siggel} siggel - the siggel to be done
 * @transaction
 */
function certify(siggel) {
    var c = siggel.company;
    var b = siggel.bag;
    if(b.siggel == null){
        b.siggel = [];
    }
    b.siggel.push(siggel); //Siggel der Ware wird hinzugefügt
    return getAssetRegistry('org.example.rewe.Bag')
        .then(function(BagRegistry){
            return BagRegistry.update(b);
        });

}