package ua.softserveinc.tc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ua.softserveinc.tc.constants.ColumnConstants.BookingConst;
import ua.softserveinc.tc.dao.BookingDao;
import ua.softserveinc.tc.entity.Booking;
import ua.softserveinc.tc.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BookingServiceImpl extends BaseServiceImpl<Booking> implements BookingService
{
    @Autowired
    private BookingDao bookingDao;

    @Override
    public List<Booking> getBookingsByRangeOfTime(String startDate, String endDate)
    {
        EntityManager entityManager = bookingDao.getEntityManager();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Booking> query = builder.createQuery(Booking.class);

        Root<Booking> root = query.from(Booking.class);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try
        {
            query.where(builder.between(root.get("bookingStartTime"),
                    dateFormat.parse(startDate), dateFormat.parse(endDate)));
        }
        catch (ParseException e)
        {
            System.out.println("Wrong format of date " + e.getMessage());
        }

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<Booking> getActiveUsersForRangeOfTime(String startDate, String endDate)
    {
        EntityManager entityManager = bookingDao.getEntityManager();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Booking> query = builder.createQuery(Booking.class);

        Root<Booking> root = query.from(Booking.class);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try
        {
            query.where(builder.between(root.get("bookingStartTime"),
                    dateFormat.parse(startDate), dateFormat.parse(endDate)))
                    .groupBy(root.get("idUser"));
        }
        catch (ParseException e)
        {
            System.out.println("Wrong format of date " + e.getMessage());
        }

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<Booking> getBookingsByUserByRangeOfTime(User user, String startDate, String endDate)
    {
        EntityManager entityManager = bookingDao.getEntityManager();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Booking> query = builder.createQuery(Booking.class);

        Root<Booking> root = query.from(Booking.class);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try
        {
            query.where(builder.between(root.get("bookingStartTime"),
                    dateFormat.parse(startDate), dateFormat.parse(endDate)),
                    builder.equal(root.get("idUser"), user))
                    .orderBy(builder.asc(root.get("bookingStartTime")));
        }
        catch (ParseException e)
        {
            System.out.println("Wrong format of date " + e.getMessage());
        }

        return entityManager.createQuery(query).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Booking> getBookingsOfThisDay()
    {
        //Date date = new Date();
        EntityManager entityManager = bookingDao.getEntityManager();
        List<Booking> bookingsDay = (List<Booking>) entityManager.createQuery(
                "from Booking where " + BookingConst.BOOKING_START_TIME + " = '2015-04-04 00:00:00'")
                .getResultList();
        return bookingsDay;
    }

    @Override
    public String getCurrentDate()
    {
        Calendar calendar = Calendar.getInstance();
        String dateNow = calendar.get(Calendar.YEAR) + "-";
        dateNow += String.format("%02d", calendar.get(Calendar.MONTH) + 1) + "-";
        dateNow += String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
        return dateNow;
    }

    @Override
    public String getDateMonthAgo()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        String dateThen = calendar.get(Calendar.YEAR) + "-";
        dateThen += String.format("%02d", calendar.get(Calendar.MONTH) + 1) + "-";
        dateThen += String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
        return dateThen;
    }

    @Override
    public HashMap<User, Integer> generateAReport(List<Booking> bookings)
    {
        HashMap<User, Integer> result = new HashMap<>();

        // get set of unique users
        HashSet<User> users = new HashSet<>();
        bookings.forEach(booking -> users.add(booking.getIdUser()));

        // get sum total for each user
        for (User user : users)
        {
            Integer sum = 0;
            for (Booking booking : bookings)
            {
                if (booking.getIdUser().equals(user))
                {
                    sum += booking.getSum(booking.getDuration());
                }
            }
            result.put(user, sum);
        }
        return result;
    }
}
