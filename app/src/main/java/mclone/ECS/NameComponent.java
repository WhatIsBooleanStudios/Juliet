package mclone.ECS;

import com.artemis.Component;


public class NameComponent extends Component {

    public NameComponent(){};
    public NameComponent(String name) {
        this.name = name;
    }

    public String name;
}
