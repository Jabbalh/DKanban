var app = angular.module("ngDraggable", []);
app.directive('draggable', function() {
  return function(scope, element) {
    // this gives us the native JS object
    var el = element[0];
    
    el.draggable = true;
    
    el.addEventListener(
      'dragstart',
      function(e) {
        e.dataTransfer.effectAllowed = 'move';
        e.dataTransfer.setData('Text', this.id);
        this.classList.add('drag');
        return false;
      },
      false
    );
    
    el.addEventListener(
      'dragend',
      function(e) {
        this.classList.remove('drag');
        return false;
      },
      false
    );
  }
});

app.directive('droppable',['$parse', function($parse,$animate) {
  return {
    scope: {
    	drop: '&drop',
    	dropdelete:'&dropdelete'
    },
    link: function(scope, element, attrs) {
      // again we need the native object
      var el = element[0];
      
      el.addEventListener(
        'dragover',
        function(e) {
          e.dataTransfer.dropEffect = 'move';
          // allows us to drop
          if (e.preventDefault) e.preventDefault();
          this.classList.add('over');
          return false;
        },
        false
      );
      
      el.addEventListener(
        'dragenter',
        function(e) {
          this.classList.add('over');
          return false;
        },
        false
      );
      
      el.addEventListener(
        'dragleave',
        function(e) {
          this.classList.remove('over');
          return false;
        },
        false
      );
      
      el.addEventListener(
        'drop',
        function(e) {
          // Stops some browsers from redirecting.
          //if (e.stopPropagation) e.stopPropagation();
          e.stopPropagation();
          //console.log("drop function -> " + f);
          this.classList.remove('over');
          var id = e.dataTransfer.getData('Text');
          var item = document.getElementById(id);
          if (attrs.dropdelete) {
        	  
          } else {
        	  //this.appendChild(item);
          }
          
          var dataResult = {};
          dataResult.targetId = this.id;
          dataResult.originId = id;
          // call the drop passed drop function
          //scope.$emit(attrs.drop,data);
          scope.drop({data : dataResult});
          id ="app/index.html#/kanban";
          return true;
        },
        false
      );
    }
  }
}]);