package net.basilwang.sever;

import java.util.ArrayList;
import java.util.Calendar;
import net.basilwang.SliderMenuFragment;
import net.basilwang.dao.SemesterService;
import net.basilwang.entity.Semester;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import android.os.AsyncTask;

public class RequestNewMessage extends
		AsyncTask<Object, Object, ArrayList<Message>> {

	private MessageService messageService;
	private SemesterService semeter;
	SliderMenuFragment menuFragment;
	private String token;

	public RequestNewMessage(SliderMenuFragment sliderMenu) {
		this.menuFragment = sliderMenu;
	}

	@Override
	protected void onPostExecute(ArrayList<Message> result) {
		int maxId = messageService.selectMaxId();
		int num=0;
		for (int i = 0; i < result.size(); i++) {
			result.get(i).getMessageContent()
					.setMessageId(result.get(i).getId());
			result.get(i).getMessageContent()
					.setCreateTime(result.get(i).getCreateTime());
			result.get(i).getMessageContent().setIsRead(0);
			if (result.get(i).getMessageContent().getMessageId() > maxId) {
				messageService.save(result.get(i).getMessageContent());
				num++;
			}
			updateSermerBeginByMessage(result.get(i).getMessageContent()
					.getContent());
			if(result.get(i).getMessageContent().getContent().equals("教务系统异常，暂时关闭课表、成绩下载功能")){
				menuFragment.isUnusual(1);
			}
			if(result.get(i).getMessageContent().getContent().equals("教务系统恢复了，可以下课表、查成绩了")){
				menuFragment.isUnusual(0);
			}

		}
		menuFragment.refresh(num);
		super.onPostExecute(result);
	}

	private void updateSermerBeginByMessage(String messageContent) {
		int semeserStart = 16;
		int semeserEnd = 49;
		int size = messageContent.indexOf("：") + 1;
		if (messageContent.length() > 16) {
			if (messageContent.substring(0, size).equals("系统已自动将您的学期时间设置为：")) {
				updateSemesterBegin(messageContent.substring(semeserStart,
						semeserEnd));
			}
		}
	}

	@Override
	protected ArrayList<Message> doInBackground(Object... params) {
		String url = (String) params[0];
		messageService = (MessageService) params[1];
		semeter = (SemesterService) params[2];
		token=(String)params[3];
		String result;
		ArrayList<Message> list1 = new ArrayList<Message>();
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		try {
			post.addHeader("X-Token", token);
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				result=filter(EntityUtils.toString(response.getEntity()));
				list1 = jsonData(result);
			}
		} catch (Exception e) {

		}
		return list1;
	}

	public String filter(String s){
		String s1 = s.replace("\\", "");
		String s2 = s1.substring(1, s1.length() - 1);
		String s3 = s2.replace(" ", "");
		String result = s3.replace("rn", "");
		return result;
	}
	public ArrayList<Message> jsonData(String str) {
		ArrayList<Message> list2 = JSON.parseObject(str,
				new TypeReference<ArrayList<Message>>() {
				});
		return list2;
	}

	public void updateSemesterBegin(String begin) {
		Calendar bcal = Calendar.getInstance();
		Calendar ecal = Calendar.getInstance();
		int bYear = Integer.parseInt(begin.substring(0, 4));
		int bMonth = Integer.parseInt(begin.substring(5, 7)) - 1;
		int bDay = Integer.parseInt(begin.substring(8, 10));
		int eYear = Integer.parseInt(begin.substring(11, 15));
		int eMonth = Integer.parseInt(begin.substring(16, 18)) - 1;
		int eDay = Integer.parseInt(begin.substring(19, 21));
		String name = begin.substring(22, 33);
		bcal.set(bYear, bMonth, bDay);
		ecal.set(eYear, eMonth, eDay);
		Semester sem = new Semester();
		sem.setBeginDate(bcal.getTime().getTime());
		sem.setEndDate(ecal.getTime().getTime());
		sem.setName(name);
		semeter.updateBeginAndEndDataOfSemester(sem);
	}
}
