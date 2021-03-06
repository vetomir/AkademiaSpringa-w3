package pl.gregorymartin.akademiaspringaw3.service;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.RestController;
import pl.gregorymartin.akademiaspringaw3.controller.CarController;
import pl.gregorymartin.akademiaspringaw3.model.Car;
import pl.gregorymartin.akademiaspringaw3.model.CarRepo;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
public
class HateoasService {
    private CarRepo repo;

    //

    public HateoasService(CarRepo repo) {
        this.repo = repo;

    }

    //

    @EventListener(ApplicationReadyEvent.class)
    public boolean hateoasForFullRepository(){
        List<Car> cars = repo.findAll();
        Integer anyCarId = cars.stream()
                .findAny()
                .get().getId();

        boolean singleLink = repo.findAll()
                .get(anyCarId)
                .hasLinks();

        if(!singleLink){
            cars.forEach(
                    x -> x.add(linkTo(CarController.class)
                            .slash(x.getId())
                            .withSelfRel()
                    ));
        }
        Link link = linkTo(CarController.class).withSelfRel();
        CollectionModel<Car> carListEntityModel = new CollectionModel(cars,link);
        return true;
    }

    //

    public EntityModel hateoasForSingleObject(Car car){
        car.add(linkTo(CarController.class).slash(car.getId()).withSelfRel());
        Link link = linkTo(CarController.class).withSelfRel();
        return new EntityModel(car,link);
    }
}
